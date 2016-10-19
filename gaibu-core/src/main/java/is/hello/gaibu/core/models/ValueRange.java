package is.hello.gaibu.core.models;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jnorgan on 9/27/16.
 */
public class ValueRange {

  @JsonProperty("min")
  public final Integer min;

  @JsonProperty("max")
  public final Integer max;

  @JsonProperty("default")
  public final Integer defaultValue;

  @JsonCreator
  public ValueRange(@JsonProperty("min")final Integer min,
                    @JsonProperty("max")final Integer max,
                    @JsonProperty("defaultValue")final Integer defaultValue) {
    this.min = min;
    this.max = max;
    this.defaultValue = defaultValue;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(ValueRange.class)
        .add("min", min)
        .add("max", max)
        .add("default_value", defaultValue)
        .toString();
  }

  public static ValueRange empty() {
    return new ValueRange(0, 0, 0);
  }
}
