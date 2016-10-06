package is.hello.gaibu.homeauto.clients;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import is.hello.gaibu.core.models.Configuration;
import is.hello.gaibu.homeauto.interceptors.HeaderInterceptor;
import is.hello.gaibu.homeauto.interceptors.PathParamsInterceptor;
import is.hello.gaibu.homeauto.interfaces.ColoredLight;
import is.hello.gaibu.homeauto.interfaces.HomeAutomationExpansion;
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

  public void setStateValues(Map<String, Object> stateValues) {
    final Call<Map<String, Object>> configCall = service.setGroupState(stateValues);
    try {
      final Response<Map<String, Object>> configResponse = configCall.execute();
      if(!configResponse.isSuccessful()) {
        LOGGER.error("error=set-state-values-failure");
      }
    } catch (IOException e) {
      LOGGER.error("error=hue-get-bridges msg={}", e.getMessage());
    }
  }

  public void setBrightness(final int value) {
    //brightness is a unit8
    final Integer brightValue = value & 0xFF;
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of("bri", brightValue));
    setStateValues(data);
  }

  public void adjustBrightness(final Integer amount) {
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of("bri_inc", amount));
    setStateValues(data);
  }

  public void adjustTemperature(final Integer amount) {
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of("ct_inc", amount));
    setStateValues(data);
  }

  public void setLightState(final Boolean isOn, final Integer transitionTime) {

    final Map<String, Object> data = Maps.newHashMap();
    data.put("on", isOn);
    data.put("transitiontime", transitionTime);

    setStateValues(data);
  }

  public void setLightState(final Boolean isOn){
    setLightState(isOn, 4);
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

    //TODO: Replace device type for environment-specific project sense-staging
    final Map<String, String> deviceInfo = Maps.newHashMap(ImmutableMap.of("devicetype", "sense-dev"));

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

  public Optional<HueScene> createDefaultScene(final Integer groupId) {
    final Optional<Map<String, HueGroup>> optionalGroupsMap = getGroups();
    if(!optionalGroupsMap.isPresent()) {
      return Optional.absent();
    }

    final Map<String, HueGroup> groupsMap = optionalGroupsMap.get();

    if(!groupsMap.containsKey(groupId.toString())) {
      LOGGER.error("error=scene-create-unknown-group group_id={}", groupId);
    }

    final HueGroup group = groupsMap.get(groupId.toString());

    final HueScene hueScene = new HueScene.Builder()
        .withName("Sense Rise")
        .withRecycle(false)
        .withLights(group.lights)
        .build();


    final Call<List<Map<String, Map<String,String>>>> createCall = service.createScene(hueScene);
    String createdSceneId = "";
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

      createdSceneId = idMap.get("id");

    } catch (IOException e) {
      LOGGER.error("error=hue-set-scene msg={}", e.getMessage());
    }

    final HueLightState defaultSceneLightState = new HueLightState.Builder()
        .withOn(true)
        .withCT(500)
        .withBrightness(254)
        .build();

    Integer successCount = group.lights.length;
    for(final String lightId : group.lights){
      final Call<List<Map<String, Map<String,String>>>> lightStateCall = service.setSceneLightState(createdSceneId, lightId, defaultSceneLightState);
      try {
        final Response<List<Map<String, Map<String,String>>>> lightStateResponse = lightStateCall.execute();
        if(!lightStateResponse.isSuccessful()) {
          successCount--;
        }
      } catch (IOException e) {
        LOGGER.error("error=scene-light-state msg={}", e.getMessage());
      }
    }
    if(successCount < 1){
      LOGGER.error("error=all-scene-lights-failed");
      return Optional.absent();
    }

    return Optional.of(hueScene);
  }
}
