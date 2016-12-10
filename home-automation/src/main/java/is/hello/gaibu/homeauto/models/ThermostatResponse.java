package is.hello.gaibu.homeauto.models;

import java.util.Map;

/**
 * Created by jnorgan on 12/9/16.
 */
public class ThermostatResponse {
  private final Map<String, Thermostat> thermostats;
  private final ResponseStatus status;

  public ThermostatResponse(final Map<String, Thermostat> thermostats, final ResponseStatus status) {
    this.thermostats = thermostats;
    this.status = status;
  }

  public Map<String, Thermostat> getThermostats() {
    return thermostats;
  }

  public ResponseStatus getStatus() {
    return status;
  }

  public static class Builder {
    private Map<String, Thermostat> thermostats;
    private ResponseStatus status;

    public Builder() {
    }

    public Builder withThermostats(final Map<String, Thermostat> thermostats) {
      this.thermostats = thermostats;
      return this;
    }

    public Builder withStatus(final ResponseStatus status) {
      this.status = status;
      return this;
    }

    public ThermostatResponse build() {
      return new ThermostatResponse(thermostats, status);
    }

  }

}
