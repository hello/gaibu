package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by jnorgan on 9/28/16.
 */
public class HueGroup {

  @JsonProperty("action")
  public final HueGroupAction action;

  @JsonProperty("class")
  public final String groupClass;

  @JsonProperty("lights")
  public final String[] lights;

  @JsonProperty("name")
  public final String name;

  @JsonProperty("state")
  @JsonIgnore
  public final Map<String, String> state;

  @JsonProperty("type")
  public final String type;

  public HueGroup(
      final HueGroupAction action,
      final String groupClass,
      final String[] lights,
      final String name,
      final Map<String, String> state,
      final String type

  ) {
    this.action = action;
    this.groupClass = groupClass;
    this.lights = lights;
    this.name = name;
    this.state = state;
    this.type = type;
  }
}
