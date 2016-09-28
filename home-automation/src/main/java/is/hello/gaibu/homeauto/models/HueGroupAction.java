package is.hello.gaibu.homeauto.models;

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

  public HueGroupAction(
      final String alert,
      final Integer brightness,
      final String colormode,
      final Integer ct,
      final String effect,
      final Integer hue,
      final Boolean on,
      final Integer sat,
      final Float[] xy) {

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
//  "alert": "none",
  //        "bri": 1,
  //        "colormode": "ct",
  //        "ct": 370,
  //        "effect": "none",
  //        "hue": 14910,
  //        "on": false,
  //        "sat": 144,
  //        "xy": [
  //    0.4596,
  //        0.4105
  //    ]
  //  },
}
