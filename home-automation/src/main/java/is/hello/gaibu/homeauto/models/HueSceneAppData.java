package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jnorgan on 9/28/16.
 */
public class HueSceneAppData {

  @JsonProperty("version")
  public final Integer version;

  @JsonProperty("data")
  public final String data;

  public HueSceneAppData(
      final Integer version,
      final String data
  ) {
    this.version = version;
    this.data = data;
  }
}
