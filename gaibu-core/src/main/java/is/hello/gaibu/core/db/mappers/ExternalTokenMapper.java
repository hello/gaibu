package is.hello.gaibu.core.db.mappers;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import is.hello.gaibu.core.models.ExternalToken;

public class ExternalTokenMapper implements ResultSetMapper<ExternalToken> {
    @Override
    public ExternalToken map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        return new ExternalToken(
                r.getString("access_token"),
                r.getString("refresh_token"),
                r.getLong("access_expires_in"),
                r.getLong("refresh_expires_in"),
                new DateTime(r.getTimestamp("created_at"), DateTimeZone.UTC),
                r.getString("device_id"),
                r.getLong("app_id")
        );
    }
}