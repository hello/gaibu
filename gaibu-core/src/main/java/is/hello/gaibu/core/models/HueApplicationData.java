package is.hello.gaibu.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HueApplicationData implements ApplicationData {

    @JsonProperty("bridge_id")
    public final String bridgeId;

    @JsonProperty("whitelist_id")
    public final String whitelistId;

    @JsonProperty("group_id")
    public final Integer groupId;

    public HueApplicationData(
        @JsonProperty("bridge_id") final String bridgeId,
        @JsonProperty("whitelist_id") final String whitelistId,
        @JsonProperty("group_id") final Integer groupId
    ) {
        this.bridgeId = bridgeId;
        this.whitelistId = whitelistId;
        this.groupId = groupId;
    }
}
