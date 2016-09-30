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
  private Boolean selected;

  @JsonProperty("id")
  public String getId() {
    return this.id;
  }

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonProperty("selected")
  public Boolean getSelected() {
    return this.selected;
  }

  public void setSelected(final Boolean selected) {
    this.selected = selected;
  }

  @JsonCreator
  public Configuration(@JsonProperty("id") final String id, @JsonProperty("name") final String name, @JsonProperty("selected") final Boolean selected) {
    this.id = id;
    this.name = name;
    this.selected = selected;
  }

}
