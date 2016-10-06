package is.hello.gaibu.homeauto.services;

import java.util.Map;

import is.hello.gaibu.homeauto.models.Thermostat;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

/**
 * Created by jnorgan on 10/5/16.
 */
public interface NestService {
  @GET("/devices/thermostats")
  Call<Map<String, Thermostat>> getThermostats();

  @GET("/devices/thermostats/{thermostat_id}")
  Call<Thermostat> getThermostat();

  @PUT("/devices/thermostats/{thermostat_id}")
  Call<Map<String, Object>> setThermostatState(@Body Map<String, Object> stateValues);

}
