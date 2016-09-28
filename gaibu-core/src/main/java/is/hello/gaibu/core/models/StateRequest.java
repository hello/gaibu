package is.hello.gaibu.core.models;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jnorgan on 9/27/16.
 */
public class StateRequest {

  @JsonProperty("state")
  public final Expansion.State state;

  @JsonCreator
  public StateRequest(@JsonProperty("state")final String state) {
    this.state = Expansion.State.valueOf(state);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(StateRequest.class)
        .add("state", state)
        .toString();
  }

}
