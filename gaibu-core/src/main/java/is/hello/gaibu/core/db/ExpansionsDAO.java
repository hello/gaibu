package is.hello.gaibu.core.db;

import com.google.common.base.Optional;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;

import java.util.List;

import is.hello.gaibu.core.db.mappers.ExpansionMapper;
import is.hello.gaibu.core.models.Expansion;

@RegisterMapper(ExpansionMapper.class)
public interface ExpansionsDAO {

    @SqlQuery("SELECT * FROM expansions WHERE id = :id")
    @SingleValueResult(Expansion.class)
    Optional<Expansion> getById(@Bind("id") Long applicationId);

    @SqlQuery("SELECT * FROM expansions WHERE service_name = :service_name")
    @SingleValueResult(Expansion.class)
    Optional<Expansion> getByServiceName(@Bind("service_name") String serviceName);

    @SqlQuery("SELECT * FROM expansions WHERE client_id = :client_id")
    @RegisterMapper(ExpansionMapper.class)
    @SingleValueResult(Expansion.class)
    Optional<Expansion> getByClientId(@Bind("client_id") String applicationClientId);

    @SqlQuery("SELECT * FROM expansions;")
    List<Expansion> getAll();

}
