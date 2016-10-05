package is.hello.gaibu.homeauto.services;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import is.hello.gaibu.core.models.Configuration;
import is.hello.gaibu.homeauto.interfaces.ColoredLight;
import is.hello.gaibu.homeauto.interfaces.HomeAutomationExpansion;
import is.hello.gaibu.homeauto.models.HueGroup;
import is.hello.gaibu.homeauto.models.HueLightState;
import is.hello.gaibu.homeauto.models.HueScene;


/**
 * Created by jnorgan on 8/24/16.
 */
public class HueLight implements ColoredLight, HomeAutomationExpansion {
  private static final Logger LOGGER = LoggerFactory.getLogger(HueLight.class);

  private String accessToken;
  private String apiPath;
  private String bridgeId;
  private String whitelistId;
  private Integer groupId;
  private String apiEndpoint;
  private final Gson gson = new Gson();

  public static String DEFAULT_API_PATH = "https://api.meethue.com/v2/";

  public HueLight(final String apiPath, final String accessToken, final String bridgeId, final String whitelistId, final Integer groupId) {
    this.accessToken = accessToken;
    this.apiPath = apiPath;
    this.bridgeId = bridgeId;
    this.whitelistId = whitelistId;
    this.groupId = groupId;

    apiEndpoint = "bridges/" + bridgeId + "/" + whitelistId + "/lights/1/state";
    if(groupId > 0) {
      apiEndpoint = "bridges/" + bridgeId + "/" + whitelistId + "/groups/" + this.groupId + "/action";
    }

  }

  public HueLight(final String apiPath, final String accessToken, final String bridgeId, final String whitelistId) {
    this(apiPath, accessToken, bridgeId, whitelistId, 0);
  }

  public HueLight(final String accessToken) {
    this(DEFAULT_API_PATH, accessToken, "", "", 0);
  }

  public HueLight(final String accessToken, final String bridgeId) {
    this(DEFAULT_API_PATH, accessToken, bridgeId, "", 0);
  }

  public void setColor() {

  }

  public void setStateValue(final String stateName, final Object stateValue) {
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of(stateName, stateValue));
    putData(apiPath + apiEndpoint, accessToken, gson.toJson(data));
  }

  public void setBrightness(final int value) {
    //brightness is a unit8
    final Integer brightValue = value & 0xFF;
    setStateValue("bri", brightValue);
  }

  public void adjustBrightness(final Integer amount) {
    setStateValue("bri_inc", amount);
  }

  public void adjustTemperature(final Integer amount) {
    setStateValue("ct_inc", amount);
  }

  public void setLightState(final Boolean isOn, final Integer transitionTime) {

    final Map<String, Object> data = Maps.newHashMap();
    data.put("on", isOn);
    data.put("transitiontime", transitionTime);

    final Optional<String> response = putData(apiPath + apiEndpoint, accessToken, gson.toJson(data));
  }
  public void setLightState(final Boolean isOn){
    setLightState(isOn, 4);
  }

  public Optional<Map<String, HueGroup>> getGroups() {
    final Optional<String> response = getData(DEFAULT_API_PATH + "bridges/" + bridgeId + "/" + whitelistId + "/groups", accessToken);
    if(!response.isPresent()) {
      LOGGER.error("error=hue-get-groups-failure bridge_id={} whitelist_id={}", bridgeId, whitelistId);
      return Optional.absent();
    }
    final Type collectionType = new TypeToken<Map<String, HueGroup>>(){}.getType();
    final Map<String, HueGroup> groupsMap = gson.fromJson(response.get(), collectionType);
    if(groupsMap.isEmpty()){
      return Optional.absent();
    }
    return Optional.of(groupsMap);
  }

  public Optional<Map<String, HueScene>> getScenes() {
    final Optional<String> response = getData(DEFAULT_API_PATH + "bridges/" + bridgeId + "/" + whitelistId + "/scenes", accessToken);
    if(!response.isPresent()) {
      LOGGER.error("error=get-groups-failure");
      return Optional.absent();
    }
    final Type collectionType = new TypeToken<Map<String, HueScene>>(){}.getType();
    final Map<String, HueScene> scenesMap = gson.fromJson(response.get(), collectionType);
    if(scenesMap.isEmpty()){
      return Optional.absent();
    }
    return Optional.of(scenesMap);
  }

  public String getBridge(final String accessToken) {
    final Optional<String> response = getData(DEFAULT_API_PATH + "bridges", accessToken);
    if(!response.isPresent()) {
      LOGGER.error("error=get-bridges-failure");
    }
    final Gson gson = new Gson();
    final Type collectionType = new TypeToken<List<Map<String, String>>>(){}.getType();
    final List<Map<String, String>> responseArray = gson.fromJson(response.get(), collectionType);
    return responseArray.get(0).get("id");
  }

  public Optional<String> getWhitelistId(final String bridgeId, final String accessToken) {

    //Do PUT request to set linkbutton state
    final Map<String, Boolean> data = Maps.newHashMap(ImmutableMap.of("linkbutton", true));

    final Gson gson = new Gson();
    putData(DEFAULT_API_PATH + "bridges/" + bridgeId + "/0/config", accessToken, gson.toJson(data));

    //Do POST to bridge with hue application name to get whitelist ID
    final String whitelistData = "{\"devicetype\": \"sense-dev\"}";
    final Optional<String> response = postData(DEFAULT_API_PATH + "bridges/" + bridgeId + "/", accessToken, whitelistData);

    if(!response.isPresent()) {
      LOGGER.error("error=get-whitelist-failure");
    }

    final Type collectionType = new TypeToken<List<Map<String, Map<String,String>>>>(){}.getType();
    final List<Map<String, Map<String,String>>> responseList = gson.fromJson(response.get(), collectionType);

    final Map<String, Map<String,String>> responseMap = responseList.get(0);

    if(responseMap.containsKey("success")) {
      final Map<String, String> successMap = responseMap.get("success");

      return Optional.of(successMap.get("username"));
    }

    return Optional.absent();
  }


  private Optional<String> getData(final String url, final String accessToken) {
    try {
      final URL uri = new URL(url);
      final HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(10000);
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Bearer " + accessToken);
      connection.setRequestProperty("Content-Type", "application/json");
      connection.connect();

      if (connection.getResponseCode() == 200) {
        final String result = readInputStream((InputStream)connection.getContent());

        return Optional.of(result);
      }
      LOGGER.error("error=get-data-failure response_code={}", connection.getResponseCode());
      return Optional.absent();

    }catch (Exception ex) {
      LOGGER.error("error=get-data-failure message='{}'", ex.getMessage());
    }

    return Optional.absent();
  }

  private Optional<String> putData(final String url, final String accessToken, final String data) {
    try {
      final URL uri = new URL(url);
      final HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
      connection.setRequestMethod("PUT");
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(10000);
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Bearer " + accessToken);
      connection.setRequestProperty("Content-Type", "application/json");

      final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
      out.write(data);
      out.close();

      final String result = readInputStream((InputStream)connection.getContent());

      return Optional.of(result);

    } catch (Exception ex) {
      LOGGER.error("error=put-data-failure message='{}'", ex.getMessage());
    }

    return Optional.absent();
  }

  private Optional<String> postData(final String url, final String accessToken, final String data) {
    try {
      final URL uri = new URL(url);
      final HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
      connection.setRequestMethod("POST");
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(10000);
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Bearer " + accessToken);
      connection.setRequestProperty("Content-Type", "application/json");

      final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
      out.write(data);
      out.close();


      final String result = readInputStream((InputStream)connection.getContent());

      return Optional.of(result);

    } catch (Exception ex) {
      LOGGER.error("error=post-data-failure message='{}'", ex.getMessage());
    }

    return Optional.absent();
  }

  private String readInputStream(final InputStream content) {
    try {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
      final StringBuilder sb = new StringBuilder();

      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
        sb.append(System.getProperty("line.separator"));
        if (!reader.ready()) {
          break;
        }
      }
      reader.close();

      return sb.toString();
    }catch (Exception ex) {
      LOGGER.error("error=inputstream-read-failure message={}", ex.getMessage());
    }
    return "";
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
    final String sceneJson = gson.toJson(hueScene);
    final Optional<String> response = postData(DEFAULT_API_PATH + "bridges/" + bridgeId + "/" + whitelistId + "/scenes/", accessToken, sceneJson);
    if(!response.isPresent()) {
      LOGGER.error("error=create-scene-failure bridge_id={} whitelist_id={}", bridgeId, whitelistId);
      return Optional.absent();
    }

    final Type collectionType = new TypeToken<List<Map<String, Map<String, String>>>>(){}.getType();
    final List<Map<String, Map<String, String>>> responseList = gson.fromJson(response.get(), collectionType);
    if(responseList.isEmpty())
    {
      return Optional.absent();
    }

    if(responseList.get(0).isEmpty()){
      return Optional.absent();
    }

    final Map<String, Map<String, String>> responseMap = responseList.get(0);
    if(!responseMap.containsKey("success")){
      LOGGER.error("error=scene-creation-error bridge_id={} whitelist_id={}", bridgeId, whitelistId);
      return Optional.absent();
    }

    final Map<String, String> idMap = responseMap.get("success");

    final String sceneId = idMap.get("id");

    final HueLightState defaultSceneLightState = new HueLightState.Builder()
        .withOn(true)
        .withCT(500)
        .withBrightness(254)
        .build();
    final String lightStateJson = gson.toJson(defaultSceneLightState);
    Integer successCount = group.lights.length;
    for(final String lightId : group.lights){
      final Optional<String> modifyResponse = putData(DEFAULT_API_PATH + "bridges/" + bridgeId + "/" + whitelistId + "/scenes/" + sceneId + "/lightstates/" + lightId, accessToken, lightStateJson);
      if(!modifyResponse.isPresent()) {
        LOGGER.error("error=modify-scene-failure bridge_id={} whitelist_id={} scene_id={} light_id={}", bridgeId, whitelistId, sceneId, lightId);
        successCount--;
      }
    }
    if(successCount < 1){
      LOGGER.error("error=all-lights-failed");
      return Optional.absent();
    }

    return Optional.of(hueScene);
  }
}
