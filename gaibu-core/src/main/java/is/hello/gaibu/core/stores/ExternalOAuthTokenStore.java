package is.hello.gaibu.core.stores;

import com.google.common.base.Optional;

import is.hello.gaibu.core.exceptions.InvalidExternalTokenException;

public interface ExternalOAuthTokenStore<T> {

    void storeToken(T externalToken) throws InvalidExternalTokenException;

    Optional<T> getTokenByDeviceId(String deviceId, Long appId);

    void disable(T accessToken);

    void disableByRefreshToken(String refreshToken);

}
