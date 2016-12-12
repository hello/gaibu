package is.hello.gaibu.homeauto.models;

import java.util.Map;

/**
 * Created by jnorgan on 12/9/16.
 */
public class GroupsResponse {
  private final Map<String, HueGroup> groups;
  private final ResponseStatus status;

  public GroupsResponse(final Map<String, HueGroup> groups, final ResponseStatus status) {
    this.groups = groups;
    this.status = status;
  }

  public Map<String, HueGroup> getGroups() {
    return groups;
  }

  public ResponseStatus getStatus() {
    return status;
  }

  public static class Builder {
    private Map<String, HueGroup> groups;
    private ResponseStatus status;

    public Builder() {
    }

    public Builder withGroups(final Map<String, HueGroup> groups) {
      this.groups = groups;
      return this;
    }

    public Builder withStatus(final ResponseStatus status) {
      this.status = status;
      return this;
    }

    public GroupsResponse build() {
      return new GroupsResponse(groups, status);
    }

  }

}
