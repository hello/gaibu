package is.hello.gaibu.homeauto.interfaces;

/**
 * Created by jnorgan on 9/1/16.
 */
public interface ControllableThermostat extends ReadableThermostat {
  Boolean setTargetTemperature(Integer temp);
}
