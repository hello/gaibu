package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jnorgan on 9/28/16.
 */
public class HueGroupAction {

  @JsonProperty("alert")
  public final String alert;

  @JsonProperty("bri")
  public final Integer brightness;

  @JsonProperty("colormode")
  public final String colormode;

  @JsonProperty("ct")
  public final Integer ct;

  @JsonProperty("effect")
  public final String effect;

  @JsonProperty("hue")
  public final Integer hue;

  @JsonProperty("on")
  public final Boolean on;

  @JsonProperty("sat")
  public final Integer sat;

  @JsonProperty("xy")
  public final Float[] xy;

  @JsonCreator
  public HueGroupAction(
      @JsonProperty("alert") final String alert,
      @JsonProperty("bri") final Integer brightness,
      @JsonProperty("colormode") final String colormode,
      @JsonProperty("ct") final Integer ct,
      @JsonProperty("effect") final String effect,
      @JsonProperty("hue") final Integer hue,
      @JsonProperty("on") final Boolean on,
      @JsonProperty("sat") final Integer sat,
      @JsonProperty("xy") final Float[] xy) {

    this.alert = alert;
    this.brightness = brightness;
    this.colormode = colormode;
    this.ct = ct;
    this.effect = effect;
    this.hue = hue;
    this.on = on;
    this.sat = sat;
    this.xy = xy;
  }
}
