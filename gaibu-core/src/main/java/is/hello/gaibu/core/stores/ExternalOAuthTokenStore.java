package is.hello.gaibu.core.stores;

import com.google.common.base.Optional;

import is.hello.gaibu.core.exceptions.InvalidExternalTokenException;
import is.hello.gaibu.core.models.Expansion;

public interface ExternalOAuthTokenStore<T> {

    void storeToken(T externalToken) throws InvalidExternalTokenException;

    Optional<T> getTokenByDeviceId(String deviceId, Long appId);

    Integer getTokenCount(String deviceId, Long appId);

    void disable(T accessToken);

    void disableByDeviceId(String deviceId, Long appId);

    void disableAllByDeviceId(String deviceId);

    void disableByRefreshToken(String refreshToken);

    Optional<String> getDecryptedExternalToken(String deviceId, Expansion expansion, Boolean isRefreshToken);

}
