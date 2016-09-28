package is.hello.gaibu.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NestApplicationData implements ApplicationData {

    @JsonProperty("thermostat_id")
    public String thermostatId;

    public NestApplicationData(@JsonProperty("thermostat_id") final String thermostatId) {
        this.thermostatId = thermostatId;
    }

    @Override
    public void setId(String id) {
        this.thermostatId = id;
    }

}
