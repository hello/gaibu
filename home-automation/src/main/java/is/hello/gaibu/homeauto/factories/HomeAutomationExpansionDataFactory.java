package is.hello.gaibu.homeauto.factories;

import com.google.common.base.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import is.hello.gaibu.core.models.Expansion;
import is.hello.gaibu.core.models.ExpansionDeviceData;
import is.hello.gaibu.homeauto.models.HueExpansionDeviceData;
import is.hello.gaibu.homeauto.models.NestExpansionDeviceData;

/**
 * Created by jnorgan on 9/27/16.
 */
public final class HomeAutomationExpansionDataFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(HomeAutomationExpansionDataFactory.class);


  public static Optional<ExpansionDeviceData> getAppData(final ObjectMapper mapper, final String data, final Expansion.ServiceName serviceName) {
    switch(serviceName) {
      case HUE:
        try {
          return Optional.of(mapper.readValue(data, HueExpansionDeviceData.class));
        } catch (IOException io) {
          LOGGER.warn("warn=missing-expansion-data expansion_name={} data='{}'", serviceName, data);
          return Optional.absent();
        }
      case NEST:
        try {
          return Optional.of(mapper.readValue(data, NestExpansionDeviceData.class));
        } catch (IOException io) {
          LOGGER.warn("warn=missing-expansion-data expansion_name={} data='{}'", serviceName, data);
          return Optional.absent();
        }
    }
    return Optional.absent();
  }

  public static Optional<ExpansionDeviceData> getEmptyAppData(final Expansion.ServiceName serviceName) {
    switch(serviceName) {
      case HUE:
        return Optional.of(new HueExpansionDeviceData("", "", 0, ""));
      case NEST:
        return Optional.of(new NestExpansionDeviceData("", ""));
    }
    return Optional.absent();
  }

}
