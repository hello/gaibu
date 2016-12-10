package is.hello.gaibu.homeauto.clients;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hello.suripu.core.models.ValueRange;
import com.hello.suripu.core.preferences.TemperatureUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import is.hello.gaibu.core.models.Capability;
import is.hello.gaibu.core.models.Configuration;
import is.hello.gaibu.core.models.ExpansionData;
import is.hello.gaibu.homeauto.interceptors.HeaderInterceptor;
import is.hello.gaibu.homeauto.interceptors.PathParamsInterceptor;
import is.hello.gaibu.homeauto.interfaces.ControllableThermostat;
import is.hello.gaibu.homeauto.interfaces.HomeAutomationExpansion;
import is.hello.gaibu.homeauto.models.AlarmActionStatus;
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

  private final NestService service;
  private final OkHttpClient client;
  private final String thermostatId;

  public static String DEFAULT_API_PATH = "https://developer-api.nest.com";
  public static Integer NEST_MIN_TEMP_C = 9;
  public static Integer NEST_MAX_TEMP_C = 32;
  public static Integer NEST_MIN_TEMP_F = 50;
  public static Integer NEST_MAX_TEMP_F = 90;
  public static Integer DEFAULT_TARGET_TEMP_C = 22;
  public static Integer TARGET_TEMP_RANGE_BUFFER = 2;
  public static Integer DEFAULT_BUFFER_TIME_SECONDS = 15 * 60; //15 mins

  public NestThermostat(final NestService service, final OkHttpClient client, final String thermostatId){
    this.service = service;
    this.client = client;
    this.thermostatId = thermostatId;
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
    return new NestThermostat(service, client, thermostatId);
  }

  public static NestThermostat create(final String apiPath, final String accessToken) {
    return NestThermostat.create(apiPath, accessToken, "");
  }

  public static NestThermostat create(final String accessToken) {
      return NestThermostat.create(DEFAULT_API_PATH, accessToken);
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
    return setTargetTemperature(temp, TemperatureUnit.CELSIUS);
  }

  public Boolean setTargetTemperature(final Integer temp, final TemperatureUnit unit) {
    final String targetValueName = (unit == TemperatureUnit.FAHRENHEIT) ? "target_temperature_f" : "target_temperature_c";
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of(targetValueName, temp));
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

  public Boolean setTargetTemperatureRange(final Integer lowTemp, final Integer highTemp, final TemperatureUnit unit) {
    final String targetValueNameHigh = (unit == TemperatureUnit.FAHRENHEIT) ? "target_temperature_high_f" : "target_temperature_high_c";
    final String targetValueNameLow = (unit == TemperatureUnit.FAHRENHEIT) ? "target_temperature_low_f" : "target_temperature_low_c";
    final Map<String, Object> data = Maps.newHashMap();
    data.put(targetValueNameHigh, highTemp);
    data.put(targetValueNameLow, lowTemp);
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
  public Optional<List<Configuration>> getConfigurations() {
    final Optional<Map<String, Thermostat>> configsMapOptional = getThermostats();
    if(!configsMapOptional.isPresent()) {
      LOGGER.error("error=get-configs-failure expansion_name=Nest");
      return Optional.absent();
    }
    final Map<String, Thermostat> thermoMap = configsMapOptional.get();

    final List<Configuration> configs = Lists.newArrayList();
    for(final Map.Entry<String, Thermostat> entry : thermoMap.entrySet()) {
      Thermostat thermostat = entry.getValue();
      final Optional<String> structureNameOptional = getStructureName(thermostat.getStructureId());
      String configName = thermostat.getName();
      if(structureNameOptional.isPresent()) {
        configName = structureNameOptional.get() + " (" + thermostat.getName() + ")";
      }

      final List<Capability> capabilities = new ArrayList<>();

      if(thermostat.getCan_cool()) {
        capabilities.add(Capability.COOL);
      }

      if(thermostat.getCan_heat()) {
        capabilities.add(Capability.HEAT);
      }

      final Configuration groupConfig = new Configuration(entry.getKey(), configName, false, capabilities);
      configs.add(groupConfig);
    }

    return Optional.of(configs);
  }

  @Override
  public Optional<Configuration> getSelectedConfiguration(final ExpansionData expansionData) {
    final ObjectMapper mapper = new ObjectMapper();
    try {
      final NestExpansionDeviceData nestData = mapper.readValue(expansionData.data, NestExpansionDeviceData.class);
      return Optional.of(new Configuration(nestData.getId(), nestData.name, true, nestData.capabilities()));
    } catch(IOException ioex) {
      return Optional.absent();
    }
  }

  @Override
  public Integer getDefaultBufferTimeSeconds() {
    return DEFAULT_BUFFER_TIME_SECONDS;
  }

  @Override
  public AlarmActionStatus runDefaultAlarmAction() {
    boolean success = setTargetTemperature(DEFAULT_TARGET_TEMP_C);
    if(success) {
      return AlarmActionStatus.OK;
    }
    return AlarmActionStatus.UNKOWN;
  }

  @Override
  public AlarmActionStatus runAlarmAction(ValueRange valueRange) {
    return setTempFromValueRange(valueRange, TemperatureUnit.CELSIUS);
  }

  public AlarmActionStatus setTempFromValueRange(final ValueRange valueRange, final TemperatureUnit unit) {
    final Integer minTempInUnits = (unit == TemperatureUnit.FAHRENHEIT) ? NEST_MIN_TEMP_F : NEST_MIN_TEMP_C;
    final Integer maxTempInUnits = (unit == TemperatureUnit.FAHRENHEIT) ? NEST_MAX_TEMP_F : NEST_MAX_TEMP_C;

    final Optional<Thermostat> thermostatOptional = getThermostat();
    if(!thermostatOptional.isPresent()) {
      LOGGER.error("error=no-thermostat expansion_name=Nest");
      return AlarmActionStatus.NOT_FOUND;
    }

    final boolean isLocked = thermostatOptional.get().getIs_locked();
    if(isLocked) {
      LOGGER.warn("thermostat_id={} status=is_locked", thermostatId);
      return AlarmActionStatus.OFF_OR_LOCKED;
    }

    final Thermostat.HvacMode hvacMode = thermostatOptional.get().getHvac_mode();
    Boolean rangeResult = true;
    Boolean setPointResult = true;

    LOGGER.info("thermostat_id={} hvac_mode={}", thermostatId, hvacMode);
    switch(hvacMode){
      case HEAT_COOL:
      case ECO: //Don't actually know if this is valid yet
        final Integer maxTemperatureC = Math.max(minTempInUnits, Math.min(maxTempInUnits, valueRange.max));
        final Integer minTemperatureC = Math.max(minTempInUnits, Math.min(maxTempInUnits, valueRange.min));
        rangeResult = setTargetTemperatureRange(minTemperatureC, maxTemperatureC, unit);
        break;
      case HEAT:
        if(valueRange.min > maxTempInUnits) {
          LOGGER.error("error=value-range-too-high hvac_mode={} value_range_min={} max_temp_units={}", hvacMode, valueRange.min, maxTempInUnits);
          return AlarmActionStatus.INVALID_TEMP_RANGE;
        }

        setPointResult = setTargetTemperature(Math.max(minTempInUnits, Math.min(maxTempInUnits, valueRange.min)), unit);
        break;
      case COOL:
        if(valueRange.max < minTempInUnits) {
          LOGGER.error("error=value-range-too-high hvac_mode={} value_range_max={} min_temp_units={}", hvacMode, valueRange.max, minTempInUnits);
          return AlarmActionStatus.INVALID_TEMP_RANGE;
        }
        setPointResult = setTargetTemperature(Math.max(minTempInUnits, Math.min(maxTempInUnits, valueRange.max)), unit);
        break;
      case OFF:
        return AlarmActionStatus.OFF_OR_LOCKED;
      default:
        return AlarmActionStatus.INVALID_HVAC;
    }

    boolean success = setPointResult && rangeResult;
    if(success) {
      LOGGER.info("thermostat_id={} alarm_action_status={}", thermostatId, AlarmActionStatus.OK);
      return AlarmActionStatus.OK;
    }

    LOGGER.error("thermostat_id={} set_point_result={} range_result={}", thermostatId, setPointResult, rangeResult);
    return AlarmActionStatus.REMOTE_ERROR;
  }
}
