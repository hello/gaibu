package is.hello.gaibu.core.db;

import com.google.common.base.Optional;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;

import java.util.List;

import is.hello.gaibu.core.db.mappers.ExternalApplicationMapper;
import is.hello.gaibu.core.models.ExternalApplication;

@RegisterMapper(ExternalApplicationMapper.class)
public interface ExternalApplicationsDAO {

    @SqlQuery("SELECT * FROM external_oauth_applications WHERE id = :id")
    @SingleValueResult(ExternalApplication.class)
    Optional<ExternalApplication> getById(@Bind("id") Long applicationId);

    @SqlQuery("SELECT * FROM external_oauth_applications WHERE name = :name")
    @SingleValueResult(ExternalApplication.class)
    Optional<ExternalApplication> getByName(@Bind("name") String applicationName);

    @SqlQuery("SELECT * FROM external_oauth_applications WHERE client_id = :client_id")
    @RegisterMapper(ExternalApplicationMapper.class)
    @SingleValueResult(ExternalApplication.class)
    Optional<ExternalApplication> getByClientId(@Bind("client_id") String applicationClientId);

    @SqlQuery("SELECT * FROM external_oauth_applications;")
    List<ExternalApplication> getAll();

}
