package is.hello.gaibu.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NestApplicationData {

    @JsonProperty("thermostat_id")
    public final String thermostatId;

    public NestApplicationData(
        @JsonProperty("thermostat_id") final String thermostatId
    ) {
        this.thermostatId = thermostatId;
    }
}
