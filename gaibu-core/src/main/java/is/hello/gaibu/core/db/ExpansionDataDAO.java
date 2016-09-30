package is.hello.gaibu.core.db;

import com.google.common.base.Optional;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;

import is.hello.gaibu.core.db.binders.BindExpansionData;
import is.hello.gaibu.core.db.mappers.ExpansionDataMapper;
import is.hello.gaibu.core.models.ExpansionData;


@RegisterMapper(ExpansionDataMapper.class)
public interface ExpansionDataDAO {

    @SingleValueResult(ExpansionData.class)
    @SqlQuery("SELECT * FROM expansion_data WHERE app_id = :application_id AND device_id = :device_id ORDER BY created_at DESC LIMIT 1")
    Optional<ExpansionData> getAppData(@Bind("application_id") Long applicationId, @Bind("device_id") String deviceId);

    @SqlUpdate("INSERT INTO expansion_data (app_id, device_id, data, enabled) VALUES (:app_id, :device_id, :data, :enabled)")
    void insertAppData(@BindExpansionData ExpansionData appData);

    @SqlUpdate("UPDATE expansion_data SET data = :data, enabled = :enabled WHERE app_id = :app_id AND device_id = :device_id")
    void updateAppData(@BindExpansionData ExpansionData appData);

}
