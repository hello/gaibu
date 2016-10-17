package is.hello.gaibu.core.models;

import com.google.common.base.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jnorgan on 10/17/16.
 */
public class DeviceExpansionPair {

  @JsonProperty("device_id")
  public final String deviceId;

  @JsonProperty("expansion_id")
  public final Long expansionId;

  public DeviceExpansionPair(final String deviceId, final Long expansionId) {
    this.deviceId = deviceId;
    this.expansionId = expansionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeviceExpansionPair that = (DeviceExpansionPair) o;
    return Objects.equal(deviceId, that.deviceId) &&
        Objects.equal(expansionId, that.expansionId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(deviceId, expansionId);
  }
}
