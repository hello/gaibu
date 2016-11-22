package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.common.collect.Lists;
import is.hello.gaibu.core.models.Capability;
import is.hello.gaibu.core.models.ExpansionDeviceData;

import java.util.ArrayList;
import java.util.List;

public class NestExpansionDeviceData implements ExpansionDeviceData {

    @JsonProperty("thermostat_id")
    public String thermostatId;

    @JsonProperty("name")
    public String name;

    @JsonIgnore
    public List<Capability> capabilities = new ArrayList<>();

    @JsonCreator
    public NestExpansionDeviceData(@JsonProperty("thermostat_id") final String thermostatId,
                                   @JsonProperty("name") final String name,
                                   @JsonProperty("capabilities") List<Capability> capabilities) {
        this.thermostatId = thermostatId;
        this.name = name;
        if(capabilities != null) {
            this.capabilities.addAll(capabilities);
        }
    }

    public static NestExpansionDeviceData empty() {
        return new NestExpansionDeviceData("", "", Lists.newArrayList());
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

    @Override
    @JsonProperty("capabilities")
    public List<Capability> capabilities() {
        return capabilities;
    }

    @Override
    public void setCapabilities(final List<Capability> capabilities) {
        if(capabilities != null) {
            this.capabilities = capabilities;
        }
    }
}
