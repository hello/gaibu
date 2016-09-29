package is.hello.gaibu.homeauto.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.hello.gaibu.core.models.ApplicationData;
import is.hello.gaibu.core.models.Expansion;
import is.hello.gaibu.homeauto.models.HueApplicationData;
import is.hello.gaibu.homeauto.models.NestApplicationData;
import is.hello.gaibu.homeauto.interfaces.HomeAutomationExpansion;
import is.hello.gaibu.homeauto.services.HueLight;
import is.hello.gaibu.homeauto.services.NestThermostat;

/**
 * Created by jnorgan on 9/27/16.
 */
public final class HomeAutomationExpansionFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(HomeAutomationExpansionFactory.class);


  public static HomeAutomationExpansion getExpansion(final String expansionName, final ApplicationData data, final String decryptedToken) {
    switch(Expansion.ServiceName.valueOf(expansionName.toUpperCase())) {
      case HUE:
        final HueApplicationData hueData = (HueApplicationData) data;
        return new HueLight(HueLight.DEFAULT_API_PATH, decryptedToken, hueData.bridgeId, hueData.whitelistId, hueData.groupId);
      case NEST:
        final NestApplicationData nestData = (NestApplicationData) data;
        return new NestThermostat(nestData.thermostatId, NestThermostat.DEFAULT_API_PATH, decryptedToken);

    }
    return null; //TODO: DON'T RETURN NULL!!!!
  }

  public static HomeAutomationExpansion getEmptyExpansion(final String expansionName, final ApplicationData data, final String decryptedToken) {
    switch(Expansion.ServiceName.valueOf(expansionName.toUpperCase())) {
      case HUE:
        final HueApplicationData hueData = (HueApplicationData) data;
        return new HueLight(HueLight.DEFAULT_API_PATH, decryptedToken, hueData.bridgeId, hueData.whitelistId);
      case NEST:
        return new NestThermostat("", NestThermostat.DEFAULT_API_PATH, decryptedToken);

    }
    return null; //TODO: DON'T RETURN NULL!!!!
  }
}
