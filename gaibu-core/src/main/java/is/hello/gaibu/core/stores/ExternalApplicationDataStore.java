package is.hello.gaibu.core.stores;

import com.google.common.base.Optional;

public interface ExternalApplicationDataStore<A> {

    Optional<A> getAppData(Long applicationId, String deviceId);
    void insertAppData(A appData);
    void updateAppData(A appData);
}
