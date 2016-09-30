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

    public HueExpansionDeviceData(
        @JsonProperty("bridge_id") final String bridgeId,
        @JsonProperty("whitelist_id") final String whitelistId,
        @JsonProperty("group_id") final Integer groupId
    ) {
        this.bridgeId = bridgeId;
        this.whitelistId = whitelistId;
        this.groupId = groupId;
    }

    @Override
    public void setId(String id) {
        this.groupId = Integer.parseInt(id);
    }

    @Override
    public String getId() {
        return this.groupId.toString();
    }
}
