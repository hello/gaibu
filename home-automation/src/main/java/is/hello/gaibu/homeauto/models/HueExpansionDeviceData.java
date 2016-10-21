package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import is.hello.gaibu.core.models.ExpansionDeviceData;

public class HueExpansionDeviceData implements ExpansionDeviceData {

    @JsonProperty("bridge_id")
    public final String bridgeId;

    @JsonProperty("whitelist_id")
    public final String whitelistId;

    @JsonProperty("group_id")
    public Integer groupId;

    @JsonProperty("name")
    public String name;

    public HueExpansionDeviceData(
        @JsonProperty("bridge_id") final String bridgeId,
        @JsonProperty("whitelist_id") final String whitelistId,
        @JsonProperty("group_id") final Integer groupId,
        @JsonProperty("name") final String name
    ) {
        this.bridgeId = bridgeId;
        this.whitelistId = whitelistId;
        this.groupId = groupId;
        this.name = name;
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
}
