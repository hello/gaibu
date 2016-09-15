package is.hello.gaibu.core.db.mappers;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import is.hello.gaibu.core.models.ExternalAuthorizationState;

public class ExternalAuthorizationStateMapper implements ResultSetMapper<ExternalAuthorizationState> {
    @Override
    public ExternalAuthorizationState map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        return new ExternalAuthorizationState(
                UUID.fromString(r.getString("auth_state")),
                new DateTime(r.getTimestamp("created_at"), DateTimeZone.UTC),
                r.getString("device_id"),
                r.getLong("app_id")
        );
    }
}
