package is.hello.gaibu.homeauto.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.hello.gaibu.core.models.ExpansionDeviceData;
import is.hello.gaibu.core.models.Expansion;
import is.hello.gaibu.homeauto.models.HueExpansionDeviceData;
import is.hello.gaibu.homeauto.models.NestExpansionDeviceData;
import is.hello.gaibu.homeauto.interfaces.HomeAutomationExpansion;
import is.hello.gaibu.homeauto.services.HueLight;
import is.hello.gaibu.homeauto.services.NestThermostat;

/**
 * Created by jnorgan on 9/27/16.
 */
public final class HomeAutomationExpansionFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(HomeAutomationExpansionFactory.class);


  public static HomeAutomationExpansion getExpansion(final String expansionName, final ExpansionDeviceData data, final String decryptedToken) {
    switch(Expansion.ServiceName.valueOf(expansionName.toUpperCase())) {
      case HUE:
        final HueExpansionDeviceData hueData = (HueExpansionDeviceData) data;
        return new HueLight(HueLight.DEFAULT_API_PATH, decryptedToken, hueData.bridgeId, hueData.whitelistId, hueData.groupId);
      case NEST:
        final NestExpansionDeviceData nestData = (NestExpansionDeviceData) data;
        return new NestThermostat(nestData.thermostatId, NestThermostat.DEFAULT_API_PATH, decryptedToken);

    }
    return null; //TODO: DON'T RETURN NULL!!!!
  }

  public static HomeAutomationExpansion getEmptyExpansion(final Expansion.ServiceName serviceName, final ExpansionDeviceData data, final String decryptedToken) {
    switch(serviceName) {
      case HUE:
        final HueExpansionDeviceData hueData = (HueExpansionDeviceData) data;
        return new HueLight(HueLight.DEFAULT_API_PATH, decryptedToken, hueData.bridgeId, hueData.whitelistId);
      case NEST:
        return new NestThermostat("", NestThermostat.DEFAULT_API_PATH, decryptedToken);

    }
    return null; //TODO: DON'T RETURN NULL!!!!
  }
}
