package is.hello.gaibu.core.db;

import com.google.common.base.Optional;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;

import is.hello.gaibu.core.db.binders.BindExternalApplicationData;
import is.hello.gaibu.core.db.mappers.ExternalApplicationDataMapper;
import is.hello.gaibu.core.models.ExternalApplicationData;


@RegisterMapper(ExternalApplicationDataMapper.class)
public interface ExternalApplicationDataDAO {

    @SingleValueResult(ExternalApplicationData.class)
    @SqlQuery("SELECT * FROM external_application_data WHERE app_id = :application_id AND device_id = :device_id ORDER BY created_at DESC LIMIT 1")
    Optional<ExternalApplicationData> getAppData(@Bind("application_id") Long applicationId, @Bind("device_id") String deviceId);

    @SqlUpdate("INSERT INTO external_application_data (app_id, device_id, data, enabled) VALUES (:app_id, :device_id, :data, :enabled)")
    void insertAppData(@BindExternalApplicationData ExternalApplicationData appData);

    @SqlUpdate("UPDATE external_application_data SET data = :data, enabled = :enabled WHERE app_id = :app_id AND device_id = :device_id")
    void updateAppData(@BindExternalApplicationData ExternalApplicationData appData);

}
