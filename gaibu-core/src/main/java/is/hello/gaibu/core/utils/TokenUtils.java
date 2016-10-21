package is.hello.gaibu.core.utils;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import com.hello.suripu.core.speech.interfaces.Vault;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import is.hello.gaibu.core.exceptions.InvalidExternalTokenException;
import is.hello.gaibu.core.models.Expansion;
import is.hello.gaibu.core.models.ExternalToken;
import is.hello.gaibu.core.stores.ExternalOAuthTokenStore;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jnorgan on 10/18/16.
 */
public class TokenUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtils.class);

  public static Optional<String> getDecryptedExternalToken(final ExternalOAuthTokenStore<ExternalToken> externalTokenStore,
                                          final Vault tokenKMSVault,
                                          final String deviceId,
                                          final Expansion expansion,
                                          final Boolean isRefreshToken) {

    final Optional<ExternalToken> externalTokenOptional = externalTokenStore.getTokenByDeviceId(deviceId, expansion.id);
    if(!externalTokenOptional.isPresent()) {
      LOGGER.warn("warning=token-not-found");
      return Optional.absent();
    }

    ExternalToken externalToken = externalTokenOptional.get();

    //check for expired token and attempt refresh
    if(externalToken.hasExpired(DateTime.now(DateTimeZone.UTC))) {
      LOGGER.error("error=token-expired device_id={}", deviceId);
      final Optional<ExternalToken> refreshedTokenOptional = TokenUtils.refreshToken(externalTokenStore, tokenKMSVault, deviceId, expansion, externalToken);
      if(!refreshedTokenOptional.isPresent()){
        LOGGER.error("error=token-refresh-failed device_id={}", deviceId);
        return Optional.absent();
      }

      externalToken = refreshedTokenOptional.get();
    }

    final Map<String, String> encryptionContext = Maps.newHashMap();
    encryptionContext.put("application_id", externalToken.appId.toString());
    final Optional<String> decryptedTokenOptional;
    if(isRefreshToken) {
      decryptedTokenOptional = tokenKMSVault.decrypt(externalToken.refreshToken, encryptionContext);
    } else {
      decryptedTokenOptional = tokenKMSVault.decrypt(externalToken.accessToken, encryptionContext);
    }


    if(!decryptedTokenOptional.isPresent()) {
      LOGGER.error("error=token-decryption-failure device_id={}", deviceId);
      return Optional.absent();
    }
    return Optional.of(decryptedTokenOptional.get());
  }

  public static Optional<ExternalToken> refreshToken(final ExternalOAuthTokenStore<ExternalToken> externalTokenStore,
                                                     final Vault tokenKMSVault,
                                                     final String deviceId,
                                                     final Expansion expansion,
                                                     final ExternalToken externalToken) {
    LOGGER.debug("action=token-refresh device_id={}", deviceId);

    final Map<String, String> encryptionContext = Maps.newHashMap();
    encryptionContext.put("application_id", externalToken.appId.toString());

    final Optional<String> decryptedRefreshTokenOptional = tokenKMSVault.decrypt(externalToken.refreshToken, encryptionContext);
    if(!decryptedRefreshTokenOptional.isPresent()) {
      LOGGER.error("error=refresh-decrypt-failed device_id={}", deviceId);
      return Optional.absent();
    }
    final String decryptedRefreshToken = decryptedRefreshTokenOptional.get();

    //Hue documentation does NOT mention that this needs to be done for token refresh, but it does
    final String clientCreds = expansion.clientId + ":" + expansion.clientSecret;
    final byte[] encodedBytes = Base64.encodeBase64(clientCreds.getBytes());
    final String encodedClientCreds = new String(encodedBytes);

    final OkHttpClient client = new OkHttpClient.Builder()
        .followSslRedirects(true)
        .followRedirects(true)
        .build();

    RequestBody formBody = new FormBody.Builder()
        .add("refresh_token", decryptedRefreshToken)
        .add("client_id", expansion.clientId)
        .add("client_secret", expansion.clientSecret)
        .build();

    Request request = new Request.Builder()
        .url(expansion.refreshURI)
        .addHeader("Authorization", "Basic " + encodedClientCreds)
        .post(formBody)
        .build();

    final Map<String, String> responseJson = Maps.newHashMap();
    try {
      final Response response = client.newCall(request).execute();
      if(!response.isSuccessful()) {
        LOGGER.error("error=token-refresh-failure");
        return Optional.absent();
      }
      final Gson gson = new Gson();
      final Type collectionType = new TypeToken<Map<String, String>>(){}.getType();
      responseJson.putAll(gson.fromJson(response.body().charStream(), collectionType));

    } catch (IOException ioex) {
      LOGGER.error("error=token-refresh-failure");
      return Optional.absent();
    }

    if(!responseJson.containsKey("access_token")) {
      LOGGER.error("error=no-access-token-returned");
      return Optional.absent();
    }

    //Invalidate current token
    externalTokenStore.disableByRefreshToken(externalToken.refreshToken);

    final Optional<String> encryptedTokenOptional = tokenKMSVault.encrypt(responseJson.get("access_token"), encryptionContext);
    if (!encryptedTokenOptional.isPresent()) {
      LOGGER.error("error=access-token-encryption-failure");
      return Optional.absent();
    }

    //Store the access_token & refresh_token (if exists)
    final ExternalToken.Builder tokenBuilder = new ExternalToken.Builder()
        .withAccessToken(encryptedTokenOptional.get())
        .withAppId(expansion.id)
        .withDeviceId(deviceId);

    if(responseJson.containsKey("expires_in")) {
      tokenBuilder.withAccessExpiresIn(Long.parseLong(responseJson.get("expires_in")));
    }

    if(responseJson.containsKey("access_token_expires_in")) {
      tokenBuilder.withAccessExpiresIn(Long.parseLong(responseJson.get("access_token_expires_in")));
    }

    if(responseJson.containsKey("refresh_token")) {
      final Optional<String> encryptedRefreshTokenOptional = tokenKMSVault.encrypt(responseJson.get("refresh_token"), encryptionContext);
      if (!encryptedRefreshTokenOptional.isPresent()) {
        LOGGER.error("error=refresh-token-encryption-failure");
        return Optional.absent();
      }
      tokenBuilder.withRefreshToken(encryptedRefreshTokenOptional.get());
    }

    if(responseJson.containsKey("refresh_token_expires_in")) {
      tokenBuilder.withRefreshExpiresIn(Long.parseLong(responseJson.get("refresh_token_expires_in")));
    }

    final ExternalToken newExternalToken = tokenBuilder.build();

    //Store the externalToken
    try {
      externalTokenStore.storeToken(newExternalToken);
      return Optional.of(newExternalToken);
    } catch (InvalidExternalTokenException ie) {
      LOGGER.error("error=token-not-saved");
      return Optional.absent();
    }
  }
}
