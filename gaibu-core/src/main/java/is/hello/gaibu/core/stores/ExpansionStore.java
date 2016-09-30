package is.hello.gaibu.core.stores;

import com.google.common.base.Optional;

import java.util.List;

public interface ExpansionStore<A> {

    Optional<A> getApplicationById(Long applicationId);
    Optional<A> getApplicationByClientId(String clientId);
    Optional<A> getApplicationByName(String applicationName);
    List<A> getAll();
}
