package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by jnorgan on 9/28/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HueLightState {

  public enum LightEffect {
    NONE("none"),
    COLORLOOP("colorloop");

    private final String effect;

    LightEffect(String effect) {
      this.effect = effect;
    }

    @JsonValue
    public String value() {
      return effect;
    }

    @JsonCreator
    public static LightEffect forValue(String value) {
      for (LightEffect le : LightEffect.values()) {
        if (le.effect.equals(value)) {
          return le;
        }
      }
      throw new IllegalArgumentException("Invalid light effect: " + value);
    }

    @Override
    public String toString() {
      return this.effect;
    }
  }

  @JsonProperty("on")
  public final Boolean on;

  @JsonProperty("bri")
  public final Integer brightness;

  @JsonProperty("hue")
  public final Integer hue;

  @JsonProperty("sat")
  public final Integer saturation;

  @JsonProperty("xy")
  public final Float[] xy;

  @JsonProperty("ct")
  public final Integer ct;

  @JsonProperty("effect")
  public final LightEffect effect;

  @JsonProperty("transitiontime")
  public final Integer transitionTime;

  public HueLightState(
      final Boolean on,
      final Integer brightness,
      final Integer hue,
      final Integer saturation,
      final Float[] xy,
      final Integer ct,
      final LightEffect effect,
      final Integer transitionTime
  ) {
    this.on = on;
    this.brightness = brightness;
    this.hue = hue;
    this.saturation = saturation;
    this.xy = xy;
    this.ct = ct;
    this.effect = effect;
    this.transitionTime = transitionTime;
  }

  public static class Builder {
    private Boolean on;
    private Integer brightness;
    private Integer hue;
    private Integer saturation;
    private Float[] xy;
    private Integer ct;
    private LightEffect effect;
    private Integer transitionTime;

    public Builder() {
    }

    public Builder withOn(final Boolean on) {
      this.on = on;
      return this;
    }

    public Builder withBrightness(final Integer brightness) {
      this.brightness = brightness;
      return this;
    }

    public Builder withHue(final Integer hue) {
      this.hue = hue;
      return this;
    }

    public Builder withSaturation(final Integer saturation) {
      this.saturation = saturation;
      return this;
    }

    public Builder withXY(final Float[] xy) {
      this.xy = xy;
      return this;
    }

    public Builder withCT(final Integer ct) {
      this.ct = ct;
      return this;
    }

    public Builder withLightEffect(final LightEffect effect) {
      this.effect = effect;
      return this;
    }

    public Builder withTransitionTime(final Integer transitionTime) {
      this.transitionTime = transitionTime;
      return this;
    }


    public HueLightState build() {
      return new HueLightState(on, brightness, hue, saturation, xy, ct, effect, transitionTime);
    }
  }
}
