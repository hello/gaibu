package is.hello.gaibu.core.db.mappers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hello.suripu.core.models.ValueRange;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import is.hello.gaibu.core.models.Expansion;
import is.hello.gaibu.core.models.MultiDensityImage;

public class ExpansionMapper implements ResultSetMapper<Expansion>{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpansionMapper.class);
    @Override
    public Expansion map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        final ObjectMapper mapper = new ObjectMapper();
        MultiDensityImage icon = MultiDensityImage.empty();
        final String iconJson = r.getString("icon");

        try {
            icon = mapper.readValue(iconJson, MultiDensityImage.class);
        } catch (IOException e) {
            LOGGER.error("error=json-deserialize-failure-icon, expansion_id={}.", r.getLong("id"));
        }

        return new Expansion(
                r.getLong("id"),
                Expansion.ServiceName.valueOf(r.getString("service_name").toUpperCase()),
                r.getString("device_name"),
                r.getString("company_name"),
                r.getString("description"),
                icon,
                r.getString("client_id"),
                r.getString("client_secret"),
                r.getString("api_uri"),
                r.getString("auth_uri"),
                r.getString("token_uri"),
                r.getString("refresh_uri"),
                Expansion.Category.fromString(r.getString("category")),
                new DateTime(r.getTimestamp("created")),
                r.getInt("grant_type"),
                "",
                Expansion.State.NOT_CONNECTED,
                ValueRange.createEmpty()
        );
    }
}
