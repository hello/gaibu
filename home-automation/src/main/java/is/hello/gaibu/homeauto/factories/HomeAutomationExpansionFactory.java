package is.hello.gaibu.homeauto.factories;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.hello.gaibu.core.models.Expansion;
import is.hello.gaibu.core.models.ExpansionDeviceData;
import is.hello.gaibu.homeauto.interfaces.HomeAutomationExpansion;
import is.hello.gaibu.homeauto.models.HueExpansionDeviceData;
import is.hello.gaibu.homeauto.models.NestExpansionDeviceData;
import is.hello.gaibu.homeauto.clients.HueLight;
import is.hello.gaibu.homeauto.clients.NestThermostat;

/**
 * Created by jnorgan on 9/27/16.
 */
public final class HomeAutomationExpansionFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(HomeAutomationExpansionFactory.class);


  public static Optional<HomeAutomationExpansion> getExpansion(final String hueAppName, final Expansion.ServiceName serviceName, final ExpansionDeviceData data, final String decryptedToken) {
    switch(serviceName) {
      case HUE:
        final HueExpansionDeviceData hueData = (HueExpansionDeviceData) data;
        return Optional.of(HueLight.create(hueAppName, HueLight.DEFAULT_API_PATH, decryptedToken, hueData.bridgeId, hueData.whitelistId, hueData.groupId));
      case NEST:
        final NestExpansionDeviceData nestData = (NestExpansionDeviceData) data;
        return Optional.of(NestThermostat.create(NestThermostat.DEFAULT_API_PATH, decryptedToken, nestData.thermostatId));

    }
    return Optional.absent();
  }

  public static Optional<HomeAutomationExpansion> getEmptyExpansion(final String hueAppName, final Expansion.ServiceName serviceName, final ExpansionDeviceData data, final String decryptedToken) {
    switch(serviceName) {
      case HUE:
        final HueExpansionDeviceData hueData = (HueExpansionDeviceData) data;
        return Optional.of(HueLight.create(hueAppName, HueLight.DEFAULT_API_PATH, decryptedToken, hueData.bridgeId, hueData.whitelistId, HueLight.DEFAULT_GROUP_ID));
      case NEST:
        return Optional.of(NestThermostat.create(NestThermostat.DEFAULT_API_PATH, decryptedToken));

    }
    return Optional.absent();
  }
}
