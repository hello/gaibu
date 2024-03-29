package is.hello.gaibu.homeauto.services;

import is.hello.gaibu.homeauto.models.Thermostat;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.Map;

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

    @GET("/structures/{structure_id}/name")
    Call<String> getStructureName(@Path("structure_id") final String structureId);

    @DELETE("/access_tokens/{access_token}")
    Call<Boolean> delete(@Path("access_token") final String accessToken);
}
