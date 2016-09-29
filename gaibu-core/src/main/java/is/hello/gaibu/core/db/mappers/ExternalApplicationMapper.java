package is.hello.gaibu.core.db.mappers;


import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import is.hello.gaibu.core.models.ExternalApplication;

public class ExternalApplicationMapper implements ResultSetMapper<ExternalApplication>{
    @Override
    public ExternalApplication map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        return new ExternalApplication(
                r.getLong("id"),
                r.getString("name"),
                r.getString("client_id"),
                r.getString("client_secret"),
                r.getString("api_uri"),
                r.getString("auth_uri"),
                r.getString("token_uri"),
                r.getString("refresh_uri"),
                r.getString("description"),
                new DateTime(r.getTimestamp("created")),
                r.getInt("grant_type"),
                ExternalApplication.Category.fromString(r.getString("category"))
        );
    }
}
