package is.hello.gaibu.homeauto.interfaces;

import com.google.common.base.Optional;

/**
 * Created by jnorgan on 9/1/16.
 */
public interface ReadableThermostat {

  public Optional<Integer> getTemperature();
}
