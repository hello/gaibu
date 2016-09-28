package is.hello.gaibu.core.factories;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import is.hello.gaibu.core.models.ApplicationData;
import is.hello.gaibu.core.models.Expansion;
import is.hello.gaibu.core.models.HueApplicationData;
import is.hello.gaibu.core.models.NestApplicationData;

/**
 * Created by jnorgan on 9/27/16.
 */
public final class ExpansionDataFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExpansionDataFactory.class);


  public static ApplicationData getAppData(final ObjectMapper mapper, final String data, final String expansionName) {
    switch(Expansion.ServiceName.valueOf(expansionName.toUpperCase())) {
      case HUE:
        try {
          return mapper.readValue(data, HueApplicationData.class);
        } catch (IOException io) {
          LOGGER.error("error=bad-expansion-data");
        }
      case NEST:
        try {
          return mapper.readValue(data, NestApplicationData.class);
        } catch (IOException io) {
          LOGGER.error("error=bad-expansion-data");
        }
    }
    return null; //TODO: DON'T RETURN NULL!!!!
  }
}
