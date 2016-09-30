package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import is.hello.gaibu.core.models.ExpansionDeviceData;

public class NestExpansionDeviceData implements ExpansionDeviceData {

    @JsonProperty("thermostat_id")
    public String thermostatId;

    public NestExpansionDeviceData(@JsonProperty("thermostat_id") final String thermostatId) {
        this.thermostatId = thermostatId;
    }

    @Override
    public void setId(String id) {
        this.thermostatId = id;
    }

    @Override
    public String getId() {
        return this.thermostatId;
    }
}
