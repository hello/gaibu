package is.hello.gaibu.homeauto.models;

import java.util.List;

import is.hello.gaibu.core.models.Configuration;

/**
 * Created by jnorgan on 12/9/16.
 */
public class ConfigurationResponse {
  private final List<Configuration> configurations;
  private final ResponseStatus status;

  public ConfigurationResponse(final List<Configuration> configurations, final ResponseStatus status) {
    this.configurations = configurations;
    this.status = status;
  }

  public List<Configuration> getConfigurations() {
    return configurations;
  }

  public ResponseStatus getStatus() {
    return status;
  }

  public static class Builder {
    private List<Configuration> configurations;
    private ResponseStatus status;

    public Builder() {
    }

    public Builder withConfigurations(final List<Configuration> configurations) {
      this.configurations = configurations;
      return this;
    }

    public Builder withStatus(final ResponseStatus status) {
      this.status = status;
      return this;
    }

    public ConfigurationResponse build() {
      return new ConfigurationResponse(configurations, status);
    }

  }

}
