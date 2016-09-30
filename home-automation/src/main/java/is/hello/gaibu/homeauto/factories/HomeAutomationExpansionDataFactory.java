package is.hello.gaibu.homeauto.factories;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import is.hello.gaibu.core.models.ExpansionDeviceData;
import is.hello.gaibu.core.models.Expansion;
import is.hello.gaibu.homeauto.models.HueExpansionDeviceData;
import is.hello.gaibu.homeauto.models.NestExpansionDeviceData;

/**
 * Created by jnorgan on 9/27/16.
 */
public final class HomeAutomationExpansionDataFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(HomeAutomationExpansionDataFactory.class);


  public static ExpansionDeviceData getAppData(final ObjectMapper mapper, final String data, final Expansion.ServiceName serviceName) {
    switch(serviceName) {
      case HUE:
        try {
          return mapper.readValue(data, HueExpansionDeviceData.class);
        } catch (IOException io) {
          LOGGER.error("error=bad-expansion-data");
        }
      case NEST:
        try {
          return mapper.readValue(data, NestExpansionDeviceData.class);
        } catch (IOException io) {
          LOGGER.error("error=bad-expansion-data");
        }
    }
    return null; //TODO: DON'T RETURN NULL!!!!
  }

  public static ExpansionDeviceData getEmptyAppData(final Expansion.ServiceName serviceName) {
    switch(serviceName) {
      case HUE:
        return new HueExpansionDeviceData("", "", 0);
      case NEST:
        return new NestExpansionDeviceData("");
    }
    return null;
  }

}
