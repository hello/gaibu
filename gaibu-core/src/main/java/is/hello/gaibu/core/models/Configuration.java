package is.hello.gaibu.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jnorgan on 9/27/16.
 */
public class Configuration {

  @JsonProperty("id")
  public final String id;

  @JsonProperty("name")
  public final String name;

  public Configuration(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

}
