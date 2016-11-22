package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.common.collect.Lists;
import is.hello.gaibu.core.models.Capability;
import is.hello.gaibu.core.models.ExpansionDeviceData;

import java.util.ArrayList;
import java.util.List;

public class HueExpansionDeviceData implements ExpansionDeviceData {

    @JsonProperty("bridge_id")
    public final String bridgeId;

    @JsonProperty("whitelist_id")
    public final String whitelistId;

    @JsonProperty("group_id")
    public Integer groupId;

    @JsonProperty("name")
    public String name;

    @JsonIgnore
    public List<Capability> capabilities = new ArrayList<>();

    @JsonProperty("capabilities")
    public List<Capability> capabilities() {
        return capabilities;
    }

    @JsonCreator
    public HueExpansionDeviceData(
        @JsonProperty("bridge_id") final String bridgeId,
        @JsonProperty("whitelist_id") final String whitelistId,
        @JsonProperty("group_id") final Integer groupId,
        @JsonProperty("name") final String name,
        @JsonProperty("capabilities") final List<Capability> capabilities
    ) {
        this.bridgeId = bridgeId;
        this.whitelistId = whitelistId;
        this.groupId = groupId;
        this.name = name;
        if(capabilities != null) {
            this.capabilities.addAll(capabilities);
        }
    }

    public static HueExpansionDeviceData empty() {
        return new HueExpansionDeviceData("", "", 0, "", Lists.newArrayList());
    }

    @Override
    public void setId(String id) {
        this.groupId = Integer.parseInt(id);
    }

    @Override
    public String getId() {
        if(this.groupId == null) {
            return "";
        }
        return this.groupId.toString();
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
    public void setCapabilities(List<Capability> capabilities) {
        if(capabilities != null) {
            this.capabilities = capabilities;
        }
    }
}
