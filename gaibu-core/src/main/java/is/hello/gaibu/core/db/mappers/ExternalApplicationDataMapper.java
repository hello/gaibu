package is.hello.gaibu.core.db.mappers;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import is.hello.gaibu.core.models.ExternalApplicationData;

public class ExternalApplicationDataMapper implements ResultSetMapper<ExternalApplicationData> {
    @Override
    public ExternalApplicationData map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        return new ExternalApplicationData(
            r.getLong("id"),
            r.getLong("app_id"),
            r.getString("device_id"),
            r.getString("data"),
            new DateTime(r.getTimestamp("created_at"), DateTimeZone.UTC),
            new DateTime(r.getTimestamp("updated_at"), DateTimeZone.UTC),
            r.getBoolean("enabled")
        );
    }
}