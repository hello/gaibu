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
import java.util.concurrent.TimeUnit;

import is.hello.gaibu.core.models.Configuration;
import is.hello.gaibu.core.models.ExpansionData;
import is.hello.gaibu.homeauto.interceptors.HeaderInterceptor;
import is.hello.gaibu.homeauto.interceptors.PathParamsInterceptor;
import is.hello.gaibu.homeauto.interfaces.ColoredLight;
import is.hello.gaibu.homeauto.interfaces.HomeAutomationExpansion;
import is.hello.gaibu.homeauto.models.HueExpansionDeviceData;
import is.hello.gaibu.homeauto.models.HueGroup;
import is.hello.gaibu.homeauto.models.HueLightState;
import is.hello.gaibu.homeauto.models.HueScene;
import is.hello.gaibu.homeauto.services.HueService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Created by jnorgan on 8/24/16.
 */
public class HueLight implements ColoredLight, HomeAutomationExpansion {
  private static final Logger LOGGER = LoggerFactory.getLogger(HueLight.class);

  private HueService service;
  private final String appName;

  public static String DEFAULT_API_PATH = "https://api.meethue.com/";
  public static String DEFAULT_APP_NAME = "sense-dev";
  public static Integer DEFAULT_GROUP_ID = 0;
  public static Integer DEFAULT_BUFFER_TIME_SECONDS = 5 * 60;
  public static Integer DEFAULT_TARGET_BRIGHTNESS = 254;
  public static Integer HUE_MIN_BRIGHTNESS = 0;
  public static Integer HUE_MAX_BRIGHTNESS = 254;

  public HueLight(final HueService service, final String appName) {
    this.service = service;
    this.appName = appName;
  }

  public static HueLight create(final String appName, final String apiPath, final String accessToken, final String bridgeId, final String whitelistId, final Integer groupId) {
    final Map<String, String> pathParams = Maps.newHashMap();
    pathParams.put("bridge_id", bridgeId);
    pathParams.put("whitelist_id", whitelistId);
    pathParams.put("group_id", groupId.toString());

    final PathParamsInterceptor pathParamsInterceptor = new PathParamsInterceptor(pathParams);
    final HeaderInterceptor headerInterceptor = new HeaderInterceptor(accessToken);
    final OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(pathParamsInterceptor)
        .readTimeout(10, TimeUnit.SECONDS)
        .build();

    final ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    final JacksonConverterFactory converterFactory = JacksonConverterFactory.create(mapper);
    final Retrofit retrofit = new Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .client(client)
        .baseUrl(apiPath)
        .build();

    final HueService service = retrofit.create(HueService.class);
    return new HueLight(service, appName);
  }

  public static HueLight create(final String appName, final String accessToken){
    return HueLight.create(appName, DEFAULT_API_PATH, accessToken, "", "", DEFAULT_GROUP_ID);
  }

  public static HueLight create(final String appName, final String accessToken, final String bridgeId){
    return HueLight.create(appName, DEFAULT_API_PATH, accessToken, bridgeId, "", DEFAULT_GROUP_ID);
  }

  public void setColor() {

  }

  public Optional<List<Map<String, Map<String, String>>>> setStateValues(Map<String, Object> stateValues) {
    final Call<List<Map<String, Map<String, String>>>> configCall = service.setGroupState(stateValues);
    try {
      final Response<List<Map<String, Map<String, String>>>> configResponse = configCall.execute();
      if(!configResponse.isSuccessful()) {
        LOGGER.error("error=set-state-values-failure response_code={} message={}", configResponse.code(), configResponse.errorBody());
        return Optional.absent();
      }

      final List<Map<String, Map<String, String>>> responseMap = configResponse.body();
      return Optional.of(responseMap);
    } catch (IOException e) {
      LOGGER.error("error=hue-set-state-values msg={}", e.getMessage());
    }
    return Optional.absent();
  }

  public Boolean setBrightness(final int value) {
    //brightness is a unit8
    final Integer brightValue = value & 0xFF;
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of("bri", brightValue));
    final Optional<List<Map<String, Map<String, String>>>> responseMap = setStateValues(data);
    return responseMap.isPresent();
  }

  public Boolean adjustBrightness(final Integer amount) {
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of("bri_inc", amount));
    final Optional<List<Map<String, Map<String, String>>>> responseMap = setStateValues(data);
    return responseMap.isPresent();
  }

  public Boolean adjustTemperature(final Integer amount) {
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of("ct_inc", amount));
    final Optional<List<Map<String, Map<String, String>>>> responseMap = setStateValues(data);
    return responseMap.isPresent();
  }

  public Boolean setLightState(final Boolean isOn, final Integer transitionTime, final Integer brightness) {

    final Map<String, Object> data = Maps.newHashMap();
    data.put("on", isOn);
    data.put("bri", brightness);
    data.put("transitiontime", transitionTime);

    final Optional<List<Map<String, Map<String, String>>>> responseMap = setStateValues(data);
    return responseMap.isPresent();
  }

  public Boolean setLightState(final Boolean isOn){
    return setLightState(isOn, 4, DEFAULT_TARGET_BRIGHTNESS);
  }

  public Boolean setLightState(final Boolean isOn, final Integer transitionTime){
    return setLightState(isOn, transitionTime, DEFAULT_TARGET_BRIGHTNESS);
  }

  public String getBridge() {
    final Call<List<Map<String, String>>> bridgeRequest = service.getBridges();
    try {
      final Response<List<Map<String, String>>> response = bridgeRequest.execute();
      if(response.isSuccessful()) {
        return response.body().get(0).get("id");
      }
    } catch (IOException e) {
      LOGGER.error("error=hue-get-bridges msg={}", e.getMessage());
    }

    return "";
  }

  public Optional<String> getWhitelistId() {
    //Do PUT request to set linkbutton state
    final Map<String, Boolean> data = Maps.newHashMap(ImmutableMap.of("linkbutton", true));
    final Call<List<Map<String, Map<String,String>>>> configCall = service.setConfigValue(data);
    try {
      final Response<List<Map<String, Map<String,String>>>> configResponse = configCall.execute();
      if(configResponse.isSuccessful()) {
        LOGGER.info("Link Button Pressed");
      }
    } catch (IOException e) {
      LOGGER.error("error=hue-get-bridges msg={}", e.getMessage());
    }

    final Map<String, String> deviceInfo = Maps.newHashMap(ImmutableMap.of("devicetype", appName));

    final Call<List<Map<String, Map<String,String>>>> wlCall = service.requestWhiteList(deviceInfo);
    try {
      final Response<List<Map<String, Map<String,String>>>> wlResponse = wlCall.execute();
      if(wlResponse.isSuccessful()) {
        LOGGER.info("WhiteListed!");
        final List<Map<String, Map<String,String>>> responseList = wlResponse.body();

        final Map<String, Map<String,String>> responseMap = responseList.get(0);

        if(responseMap.containsKey("success")) {
          final Map<String, String> successMap = responseMap.get("success");

          return Optional.of(successMap.get("username"));
        }

      }
    } catch (IOException e) {
      LOGGER.error("error=hue-get-bridges msg={}", e.getMessage());
    }
    return Optional.absent();
  }

  public Optional<Map<String, HueGroup>> getGroups() {
    final Call<Map<String, HueGroup>> groupRequest = service.getGroups();
    try {
      final Response<Map<String, HueGroup>> response = groupRequest.execute();
      if(response.isSuccessful()) {
        final Map<String, HueGroup> groupsMap = response.body();
        if(groupsMap.isEmpty()){
          return Optional.absent();
        }
        return Optional.of(groupsMap);
      }

      LOGGER.error("error=get-groups-failure response_code={}", response.code());

      if(response.code() == 401) {

        if(response.errorBody().string().contains("access_token_expired")) {
          //Attempt token refresh
          LOGGER.error("error=token-expired");
          return Optional.absent();
        }
      }
    } catch (IOException e) {
      LOGGER.error("error=hue-get-groups msg={}", e.getMessage());
    }

    return Optional.absent();
  }

  public Optional<Map<String, HueScene>> getScenes() {
    final Call<Map<String, HueScene>> groupRequest = service.getScenes();
    try {
      final Response<Map<String, HueScene>> response = groupRequest.execute();
      if(response.isSuccessful()) {
        final Map<String, HueScene> scenesMap = response.body();
        if(scenesMap.isEmpty()){
          return Optional.absent();
        }
        return Optional.of(scenesMap);
      }
    } catch (IOException e) {
      LOGGER.error("error=hue-get-groups msg={}", e.getMessage());
    }

    return Optional.absent();
  }

  @Override
  public List<Configuration> getConfigurations() {
    final Optional<Map<String, HueGroup>> optionalGroupsMap = getGroups();
    if(!optionalGroupsMap.isPresent()) {
      return Lists.newArrayList();
    }

    final Map<String, HueGroup> groupsMap = optionalGroupsMap.get();

    final List<Configuration> configs = Lists.newArrayList();
    for(final Map.Entry<String, HueGroup> entry : groupsMap.entrySet()) {
      final Configuration groupConfig = new Configuration(entry.getKey(), entry.getValue().name, false);
      configs.add(groupConfig);
    }
    return configs;
  }

  @Override
  public Optional<Configuration> getSelectedConfiguration(final ExpansionData expansionData) {
    final ObjectMapper mapper = new ObjectMapper();
    try {
      final HueExpansionDeviceData hueData = mapper.readValue(expansionData.data, HueExpansionDeviceData.class);
      return Optional.of(new Configuration(hueData.groupId.toString(), hueData.name, true));
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
    return setLightState(true, 3000);
  }

  @Override
  public Boolean runAlarmAction(final ValueRange valueRange) {
    //clamp the brightness value
    final Integer brightnessValue = Math.max(HUE_MIN_BRIGHTNESS, Math.min(HUE_MAX_BRIGHTNESS, valueRange.min));
    return setLightState(true, 3000, brightnessValue);
  }

  public Optional<String> createScene(final String sceneName, final String[] lightIds) {
    final HueScene hueScene = new HueScene.Builder()
        .withName(sceneName)
        .withRecycle(false)
        .withLights(lightIds)
        .build();

    final Call<List<Map<String, Map<String,String>>>> createCall = service.createScene(hueScene);
    try {
      final Response<List<Map<String, Map<String,String>>>> configResponse = createCall.execute();
      if(!configResponse.isSuccessful()) {
        return Optional.absent();
      }
      final List<Map<String, Map<String,String>>> responseList = configResponse.body();
      if(responseList.isEmpty())
      {
        return Optional.absent();
      }

      if(responseList.get(0).isEmpty()){
        return Optional.absent();
      }
      final Map<String, Map<String, String>> responseMap = responseList.get(0);
      if(!responseMap.containsKey("success")){
        LOGGER.error("error=scene-creation-error");
        return Optional.absent();
      }

      final Map<String, String> idMap = responseMap.get("success");

      return Optional.of(idMap.get("id"));

    } catch (IOException e) {
      LOGGER.error("error=hue-set-scene msg={}", e.getMessage());
    }

    return Optional.absent();
  }

  public void setSceneLightStates(final String sceneId, final String[] lightIds, final HueLightState lightState) {
    for(final String lightId : lightIds){
      final Call<List<Map<String, Map<String,String>>>> lightStateCall = service.setSceneLightState(sceneId, lightId, lightState);
      try {
        final Response<List<Map<String, Map<String,String>>>> lightStateResponse = lightStateCall.execute();
        if(!lightStateResponse.isSuccessful()) {
          LOGGER.error("error=scene-light-state-failed scene_id={} light_id={}", sceneId, lightId);
        }
      } catch (IOException e) {
        LOGGER.error("error=scene-light-state msg={}", e.getMessage());
      }
    }
  }

  public Optional<String> createDefaultScene(final Integer groupId) {
    final Optional<Map<String, HueGroup>> optionalGroupsMap = getGroups();
    if(!optionalGroupsMap.isPresent()) {
      return Optional.absent();
    }

    final Map<String, HueGroup> groupsMap = optionalGroupsMap.get();

    if(!groupsMap.containsKey(groupId.toString())) {
      LOGGER.error("error=scene-create-unknown-group group_id={}", groupId);
    }

    final HueGroup group = groupsMap.get(groupId.toString());

    final Optional<String> createdSceneIdOptional = createScene("Sense Rise", group.lights);
    if(!createdSceneIdOptional.isPresent()) {
      LOGGER.error("error=scene-create-failed group_id={}", groupId);
      return Optional.absent();
    }

    final String createdSceneId = createdSceneIdOptional.get();

    final HueLightState defaultSceneLightState = new HueLightState.Builder()
        .withOn(true)
        .withCT(500)
        .withBrightness(254)
        .build();
    setSceneLightStates(createdSceneId, group.lights, defaultSceneLightState);

    return Optional.of(createdSceneId);
  }
}
