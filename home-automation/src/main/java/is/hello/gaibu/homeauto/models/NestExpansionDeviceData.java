package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import is.hello.gaibu.core.models.ExpansionDeviceData;

public class NestExpansionDeviceData implements ExpansionDeviceData {

    @JsonProperty("thermostat_id")
    public String thermostatId;

    @JsonProperty("name")
    public String name;

    public NestExpansionDeviceData(@JsonProperty("thermostat_id") final String thermostatId,
                                   @JsonProperty("name") final String name) {
        this.thermostatId = thermostatId;
        this.name = name;
    }

    @Override
    public void setId(String id) {
        this.thermostatId = id;
    }

    @Override
    public String getId() {
        if(this.thermostatId == null){
            return "";
        }

        return this.thermostatId;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
