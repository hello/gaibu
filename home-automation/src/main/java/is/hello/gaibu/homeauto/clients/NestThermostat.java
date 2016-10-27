package is.hello.gaibu.homeauto.clients;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hello.suripu.core.models.ValueRange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import is.hello.gaibu.core.models.Configuration;
import is.hello.gaibu.core.models.ExpansionData;
import is.hello.gaibu.homeauto.interceptors.HeaderInterceptor;
import is.hello.gaibu.homeauto.interceptors.PathParamsInterceptor;
import is.hello.gaibu.homeauto.interfaces.ControllableThermostat;
import is.hello.gaibu.homeauto.interfaces.HomeAutomationExpansion;
import is.hello.gaibu.homeauto.models.NestExpansionDeviceData;
import is.hello.gaibu.homeauto.models.Thermostat;
import is.hello.gaibu.homeauto.services.NestService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Created by jnorgan on 8/24/16.
 */
public class NestThermostat implements ControllableThermostat, HomeAutomationExpansion {
  private static final Logger LOGGER = LoggerFactory.getLogger(NestThermostat.class);

  private NestService service;
  private OkHttpClient client;

  public static String DEFAULT_API_PATH = "https://developer-api.nest.com";
  public static Integer NEST_MIN_TEMP_C = 9;
  public static Integer NEST_MAX_TEMP_C = 32;
  public static Integer DEFAULT_TARGET_TEMP_C = 22;
  public static Integer DEFAULT_BUFFER_TIME_SECONDS = 15 * 60; //15 mins

  public NestThermostat(final NestService service, final OkHttpClient client){
    this.service = service;
    this.client = client;
  }

  public static NestThermostat create(final String apiPath, final String accessToken, final String thermostatId) {
    final Map<String, String> pathParams = Maps.newHashMap();
    pathParams.put("thermostat_id", thermostatId);

    final PathParamsInterceptor pathParamsInterceptor = new PathParamsInterceptor(pathParams);
    final HeaderInterceptor headerInterceptor = new HeaderInterceptor(accessToken);
    final OkHttpClient client = new OkHttpClient.Builder()
        .addNetworkInterceptor(headerInterceptor) //Auth header was disappearing on redirect, this intercepts for each call
        .addInterceptor(pathParamsInterceptor)
        .followSslRedirects(true)
        .followRedirects(true)
        .build();

    final ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    final JacksonConverterFactory converterFactory = JacksonConverterFactory.create(mapper);
    final Retrofit retrofit = new Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .client(client)
        .baseUrl(apiPath)
        .build();

    final NestService service = retrofit.create(NestService.class);
    return new NestThermostat(service, client);
  }

  public static NestThermostat create(final String apiPath, final String accessToken) {
    return NestThermostat.create(apiPath, accessToken, "");
  }

  public Optional<Map<String, Object>> setStateValues(Map<String, Object> stateValues) {
    final Call<Map<String, Object>> configCall = service.setThermostatState(stateValues);
    try {
      final Response<Map<String, Object>> configResponse = configCall.execute();
      //Retrofit doesn't handle 307 redirects for non-GET requests
      //Per the Nest documentation: "you should cache the host and port number for use in future calls with that user/access token"
      //TODO: Cache redirect url for accesstoken
      if(configResponse.raw().code() == 307) {
        String location = configResponse.raw().header("Location");
        if (location != null) {
          Request request = configCall.request().newBuilder()
              .url(location)
              .build();

          final okhttp3.Response response = client.newCall(request).execute();
          if(!response.isSuccessful()) {
            LOGGER.error("error=nest-set-state-failure");
            response.close();
            return Optional.absent();
          }
          response.close();
          return Optional.of(stateValues);
        }
      }
    } catch (IOException e) {
      LOGGER.error("error=nest-set-state msg={}", e.getMessage());
    }
    return Optional.absent();
  }

  public Boolean setTargetTemperature(final Integer temp) {
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of("target_temperature_c", temp));
    final Optional<Map<String, Object>> responseMap = setStateValues(data);
    return responseMap.isPresent();
  }

  public Boolean setTargetTemperatureHigh(final Integer temp) {
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of("target_temperature_high_c", temp));
    final Optional<Map<String, Object>> responseMap = setStateValues(data);
    return responseMap.isPresent();
  }

  public Boolean setTargetTemperatureLow(final Integer temp) {
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of("target_temperature_low_c", temp));
    final Optional<Map<String, Object>> responseMap = setStateValues(data);
    return responseMap.isPresent();
  }

  public Boolean setTargetTemperatureRange(final Integer lowTemp, final Integer highTemp) {
    final Map<String, Object> data = Maps.newHashMap();
    data.put("target_temperature_high_c", highTemp);
    data.put("target_temperature_low_c", lowTemp);
    final Optional<Map<String, Object>> responseMap = setStateValues(data);
    return responseMap.isPresent();
  }

  public Optional<Integer> getTemperature() {

    final Optional<Thermostat> thermoOptional = getThermostat();
    if(!thermoOptional.isPresent()) {
      LOGGER.error("error=get-temp-failure");
      return Optional.absent();
    }
    final Thermostat thermostat = thermoOptional.get();
    if(thermostat.getAmbient_temperature_c() == null){
      return Optional.absent();
    }
    return Optional.of(thermostat.getAmbient_temperature_c().intValue());
  }

  public Optional<Thermostat.HvacMode> getMode() {

    final Optional<Thermostat> thermoOptional = getThermostat();
    if(!thermoOptional.isPresent()) {
      LOGGER.error("error=get-temp-failure");
      return Optional.absent();
    }
    final Thermostat thermostat = thermoOptional.get();
    if(thermostat.getAmbient_temperature_c() == null){
      return Optional.absent();
    }
    return Optional.of(thermostat.getHvac_mode());
  }

  public Optional<Thermostat> getThermostat() {
    final Call<Thermostat> thermoRequest = service.getThermostat();
    try {
      final Response<Thermostat> response = thermoRequest.execute();
      if(response.isSuccessful()) {
        final Thermostat thermo = response.body();
        return Optional.of(thermo);
      }
    } catch (IOException e) {
      LOGGER.error("error=nest-get-thermostat msg={}", e.getMessage());
    }

    return Optional.absent();
  }

  public Optional<Map<String, Thermostat>> getThermostats() {
    final Call<Map<String, Thermostat>> groupRequest = service.getThermostats();
    try {
      final Response<Map<String, Thermostat>> response = groupRequest.execute();
      if(response.isSuccessful()) {
        final Map<String, Thermostat> groupsMap = response.body();
        if(groupsMap.isEmpty()){
          return Optional.absent();
        }
        return Optional.of(groupsMap);
      }
    } catch (IOException e) {
      LOGGER.error("error=hue-get-groups msg={}", e.getMessage());
    }

    return Optional.absent();
  }

  public Optional<String> getStructureName(final String structureId) {
    final Call<String> structureRequest = service.getStructureName(structureId);
    try {
      final Response<String> response = structureRequest.execute();
      if(response.isSuccessful()) {
        final String structureName = response.body();
        return Optional.of(structureName);
      }
    } catch (IOException e) {
      LOGGER.error("error=nest-get-structure-name msg={}", e.getMessage());
    }

    return Optional.absent();
  }

  @Override
  public List<Configuration> getConfigurations() {
    final Optional<Map<String, Thermostat>> configsMapOptional = getThermostats();
    if(!configsMapOptional.isPresent()) {
      LOGGER.error("error=get-configs-failure expansion_name=Nest");
      return Lists.newArrayList();
    }
    final Map<String, Thermostat> thermoMap = configsMapOptional.get();

    final List<Configuration> configs = Lists.newArrayList();
    for(final Map.Entry<String, Thermostat> entry : thermoMap.entrySet()) {
      final Optional<String> structureNameOptional = getStructureName(entry.getValue().getStructureId());
      String configName = entry.getValue().getName();
      if(structureNameOptional.isPresent()) {
        configName = structureNameOptional.get() + " (" + entry.getValue().getName() + ")";
      }
      final Configuration groupConfig = new Configuration(entry.getKey(), configName, false);
      configs.add(groupConfig);
    }

    return configs;
  }

  @Override
  public Optional<Configuration> getSelectedConfiguration(final ExpansionData expansionData) {
    final ObjectMapper mapper = new ObjectMapper();
    try {
      final NestExpansionDeviceData nestData = mapper.readValue(expansionData.data, NestExpansionDeviceData.class);
      return Optional.of(new Configuration(nestData.getId(), nestData.name, true));
    } catch(IOException ioex) {
      return Optional.absent();
    }
  }

  @Override
  public Integer getDefaultBufferTimeSeconds() {
    return DEFAULT_BUFFER_TIME_SECONDS;
  }

  @Override
  public Boolean runDefaultAlarmAction() {
    return setTargetTemperature(DEFAULT_TARGET_TEMP_C);
  }

  @Override
  public Boolean runAlarmAction(ValueRange valueRange) {

    final Optional<Thermostat.HvacMode> hvacModeOptional = getMode();
    if(!hvacModeOptional.isPresent()) {
      LOGGER.error("error=hvac-mode-failure expansion_name=Nest");
      return false;
    }

    final Thermostat.HvacMode hvacMode = hvacModeOptional.get();
    Boolean rangeResult = true;
    Boolean setPointResult = true;


    switch(hvacMode.value()){
      case "heat-cool":
      case "eco": //Don't actually know if this is valid yet
        final Integer maxTemperatureC = Math.max(NEST_MIN_TEMP_C, Math.min(NEST_MAX_TEMP_C, valueRange.max));
        final Integer minTemperatureC = Math.max(NEST_MIN_TEMP_C, Math.min(NEST_MAX_TEMP_C, valueRange.min));
        rangeResult = setTargetTemperatureRange(minTemperatureC, maxTemperatureC);
        break;
      case "heat":
        if(valueRange.min > NEST_MAX_TEMP_C) {
          return false;
        }
        setPointResult = setTargetTemperature(Math.max(NEST_MIN_TEMP_C, Math.min(NEST_MAX_TEMP_C, valueRange.min)));
        break;
      case "cool":
        if(valueRange.max < NEST_MIN_TEMP_C) {
          return false;
        }
        setPointResult = setTargetTemperature(Math.max(NEST_MIN_TEMP_C, Math.min(NEST_MAX_TEMP_C, valueRange.max)));
        break;
      default:
        return false;
    }
    return setPointResult && rangeResult;
  }
}
