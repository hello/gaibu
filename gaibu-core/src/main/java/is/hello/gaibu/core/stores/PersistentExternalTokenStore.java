package is.hello.gaibu.core.stores;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import is.hello.gaibu.core.db.ExternalTokenDAO;
import is.hello.gaibu.core.exceptions.InvalidExternalTokenException;
import is.hello.gaibu.core.models.Expansion;
import is.hello.gaibu.core.models.ExternalToken;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersistentExternalTokenStore implements ExternalOAuthTokenStore<ExternalToken> {

    private final ExternalTokenDAO externalTokenDAO;
    private final ExpansionStore<Expansion> expansionStore;
    private final Vault tokenKMSVault;
    private final OkHttpClient httpClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentExternalTokenStore.class);

    final LoadingCache<String, Optional<ExternalToken>> cache;

    // This is called by the cache when it doesn't contain the key
    final CacheLoader loader = new CacheLoader<String, Optional<ExternalToken>>() {
        public Optional<ExternalToken> load(final String dirtyToken) {
            LOGGER.debug("{} not in cache, fetching from DB", dirtyToken);
            return fromDB(dirtyToken, false);
        }
    };


    public PersistentExternalTokenStore(
            final ExternalTokenDAO externalTokenDAO,
            final ExpansionStore<Expansion> expansionStore,
            final Vault tokenKMSVault,
            final OkHttpClient httpClient) {
        this.externalTokenDAO = externalTokenDAO;
        this.expansionStore = expansionStore;
        this.tokenKMSVault = tokenKMSVault;
        this.httpClient = httpClient;

        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build(loader);
    }

    @Override
    public void storeToken(ExternalToken externalToken) throws InvalidExternalTokenException {
        if(externalToken.hasExpired(DateTime.now(DateTimeZone.UTC))) {
            throw new InvalidExternalTokenException();
        }

        externalTokenDAO.storeExternalToken(externalToken);
    }

    @Override
    public Optional<ExternalToken> getTokenByDeviceId(final String deviceId, final Long appId) {
        return externalTokenDAO.getByDeviceId(deviceId, appId);
    }

    @Override
    public Integer getTokenCount(final String deviceId, final Long appId) {
        return externalTokenDAO.getTokenCount(deviceId, appId);
    }

    @Override
    public void disable(final ExternalToken externalToken) {
        externalTokenDAO.disable(externalToken.accessToken);
    }

    @Override
    public void disableByDeviceId(final String deviceId, final Long expansionId) {
        final Optional<Expansion> expansionOptional = expansionStore.getApplicationById(expansionId);
        if(!expansionOptional.isPresent()) {
            LOGGER.warn("warning=application-not-found");
        }

        final Expansion expansion = expansionOptional.get();

        // Specific Nest use case
        if (Expansion.ServiceName.NEST.equals(expansion.serviceName)) {
            try {
                final Optional<String> decryptedTokenOptional = getDecryptedExternalToken(deviceId, expansion, false);
                if(!decryptedTokenOptional.isPresent()) {
                    LOGGER.warn("action=deauth-nest result=fail-to-decrypt-token device_id={}", deviceId);
                }

                final String decryptedToken = decryptedTokenOptional.get();
                final String url = "https://api.home.nest.com/oauth2/access_tokens/" + decryptedToken;
                final Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();

                final okhttp3.Response response = httpClient.newCall(request).execute();
                response.close();
                LOGGER.info("action=deauth-nest device_id={} http_resp={} success={}", deviceId, response.code(), response.isSuccessful());
            } catch (IOException e) {
                LOGGER.error("error=deauth-nest message={}", e.getMessage());
            }
        }

        externalTokenDAO.disableByDeviceId(deviceId, expansionId);
    }

    @Override
    public void disableAllByDeviceId(final String deviceId) {
        final List<Expansion> expansions = expansionStore.getAll();
        for(final Expansion exp : expansions) {
            disableByDeviceId(deviceId, exp.id);
        }

    }

    public Optional<ExternalToken> getExternalTokenByToken(final String accessToken, final DateTime now) {
        final Optional<ExternalToken> token = cache.getUnchecked(accessToken);
        if(!token.isPresent()) {
            return Optional.absent();
        }
        if(hasExpired(token.get(), now, false)) {
            return Optional.absent();
        }

        return token;
    }

    @Override
    public void disableByRefreshToken(final String refreshToken) {
        externalTokenDAO.disableByRefreshToken(refreshToken);
    }

    private Optional<ExternalToken> fromDB(final String token, final Boolean fromRefreshToken) {

        Optional<ExternalToken> accessTokenOptional;
        if (fromRefreshToken) {
            accessTokenOptional = externalTokenDAO.getByRefreshToken(token);
        } else {
            accessTokenOptional = externalTokenDAO.getByAccessToken(token);
        }

        if(!accessTokenOptional.isPresent()) {
            LOGGER.warn("warning=token_not_found token={}", token);
            return Optional.absent();
        }

        final ExternalToken externalToken = accessTokenOptional.get();

        return accessTokenOptional;
    }

    private Boolean hasExpired(final ExternalToken externalToken, DateTime now, final Boolean isRefreshToken) {
        final Long expiresIn = (isRefreshToken) ? externalToken.refreshExpiresIn : externalToken.accessExpiresIn;
        long diffInSeconds= (now.getMillis() - externalToken.createdAt.getMillis()) / 1000;
        LOGGER.trace("external_token={} for device_id={}", externalToken.accessToken, externalToken.deviceId);
        LOGGER.trace("Token created at = {}", externalToken.createdAt);
        LOGGER.trace("DiffInSeconds = {}", diffInSeconds);
        if(diffInSeconds > expiresIn) {
            LOGGER.warn("warning=expired_token token={} device_id={} secs_since_expiration={}", externalToken.accessToken, externalToken.deviceId, diffInSeconds);
            return true;
        }

        return false;
    }

    public Optional<String> getDecryptedExternalToken(final String deviceId,
                                                             final Expansion expansion,
                                                             final Boolean isRefreshToken) {

        final Optional<ExternalToken> externalTokenOptional = getTokenByDeviceId(deviceId, expansion.id);
        if(!externalTokenOptional.isPresent()) {
            LOGGER.warn("warning=token-not-found");
            return Optional.absent();
        }

        ExternalToken externalToken = externalTokenOptional.get();

        //check for expired token and attempt refresh
        if(externalToken.hasExpired(DateTime.now(DateTimeZone.UTC))) {
            LOGGER.error("error=token-expired device_id={}", deviceId);
            final Optional<ExternalToken> refreshedTokenOptional = refreshToken(deviceId, expansion, externalToken);
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

    public Optional<ExternalToken> refreshToken(final String deviceId,
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
        disableByRefreshToken(externalToken.refreshToken);

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
            storeToken(newExternalToken);
            return Optional.of(newExternalToken);
        } catch (InvalidExternalTokenException ie) {
            LOGGER.error("error=token-not-saved");
            return Optional.absent();
        }
    }
}
