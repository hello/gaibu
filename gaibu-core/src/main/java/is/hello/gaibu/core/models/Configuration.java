package is.hello.gaibu.core.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jnorgan on 9/27/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {

  private String id;
  private String name;

  @JsonProperty("id")
  public String getId() {
    return this.id;
  }

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonCreator
  public Configuration(@JsonProperty("id") final String id, @JsonProperty("name") final String name) {
    this.id = id;
    this.name = name;
  }

}
