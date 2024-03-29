package is.hello.gaibu.core.db.mappers;


import is.hello.gaibu.core.models.ExpansionData;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExpansionDataMapper implements ResultSetMapper<ExpansionData> {
    @Override
    public ExpansionData map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        final Long accountId = r.getLong("account_id");
        final Long pairedAccountId =  accountId == 0 ? null : accountId;
        return new ExpansionData(
            r.getLong("id"),
            r.getLong("app_id"),
            r.getString("device_id"),
            r.getString("data"),
            new DateTime(r.getTimestamp("created_at"), DateTimeZone.UTC),
            new DateTime(r.getTimestamp("updated_at"), DateTimeZone.UTC),
            r.getBoolean("enabled"),
            pairedAccountId
        );
    }
}