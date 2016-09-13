package is.hello.gaibu.homeauto.services;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import is.hello.gaibu.homeauto.interfaces.ColoredLight;


/**
 * Created by jnorgan on 8/24/16.
 */
public class HueLight implements ColoredLight{

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
      apiEndpoint = "bridges/" + bridgeId + "/" + whitelistId + "/groups/" + groupId + "/action";
    }

  }

  public HueLight(final String apiPath, final String accessToken, final String bridgeId, final String whitelistId) {
    this(apiPath, accessToken, bridgeId, whitelistId, 0);
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

  public String getGroups() {
    final Optional<String> response = getData(DEFAULT_API_PATH + "bridges/" + bridgeId + "/" + whitelistId + "/groups", accessToken);
    if(!response.isPresent()) {
      System.out.printf("Error getting bridges");
    }
    return response.get();
  }

  public static String getBridge(final String accessToken) {
    final Optional<String> response = getData(DEFAULT_API_PATH + "bridges", accessToken);
    if(!response.isPresent()) {
      System.out.printf("Error getting bridges");
    }
    final Gson gson = new Gson();
    final Type collectionType = new TypeToken<List<Map<String, String>>>(){}.getType();
    final List<Map<String, String>> responseArray = gson.fromJson(response.get(), collectionType);
    return responseArray.get(0).get("id");
  }

  public static Optional<String> getWhitelistId(final String bridgeId, final String accessToken) {

    //Do PUT request to set linkbutton state
    final Map<String, Boolean> data = Maps.newHashMap(ImmutableMap.of("linkbutton", true));

    final Gson gson = new Gson();
    putData(DEFAULT_API_PATH + "bridges/" + bridgeId + "/0/config", accessToken, gson.toJson(data));

    //Do POST to bridge with hue application name to get whitelist ID
    final String whitelistData = "{\"devicetype\": \"sense-dev\"}";
    final Optional<String> response = postData(DEFAULT_API_PATH + "bridges/" + bridgeId + "/", accessToken, whitelistData);

    if(!response.isPresent()) {
      System.out.printf("Error getting whitelist ID");
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


  public static Optional<String> getData(final String url, final String accessToken) {
    try {
      final URL uri = new URL(url);
      final HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(3000);
      connection.setReadTimeout(3000);
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Bearer " + accessToken);
      connection.setRequestProperty("Content-Type", "application/json");
      connection.connect();

      if (connection.getResponseCode() == 200) {
        final String result = readInputStream((InputStream)connection.getContent());

        return Optional.of(result);
      }
      return com.google.common.base.Optional.absent();

    }catch (Exception ex) {
      System.out.printf("Connect Exception: %s", ex.getMessage());
    }

    return Optional.absent();
  }

  public static Optional<String> putData(final String url, final String accessToken, final String data) {
    try {
      final URL uri = new URL(url);
      final HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
      connection.setRequestMethod("PUT");
      connection.setConnectTimeout(3000);
      connection.setReadTimeout(3000);
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Bearer " + accessToken);
      connection.setRequestProperty("Content-Type", "application/json");

      final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
      out.write(data);
      out.close();


      final String result = readInputStream((InputStream)connection.getContent());

      return Optional.of(result);

    } catch (Exception ex) {
      System.out.printf("Failed to Put Data %s", ex.getMessage());
    }

    return Optional.absent();
  }

  public static Optional<String> postData(final String url, final String accessToken, final String data) {
    try {
      final URL uri = new URL(url);
      final HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
      connection.setRequestMethod("POST");
      connection.setConnectTimeout(3000);
      connection.setReadTimeout(3000);
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Bearer " + accessToken);
      connection.setRequestProperty("Content-Type", "application/json");

      final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
      out.write(data);
      out.close();


      final String result = readInputStream((InputStream)connection.getContent());

      return Optional.of(result);

    } catch (Exception ex) {
      System.out.printf("Failed to POST Data %s", ex.getMessage());
    }

    return Optional.absent();
  }

  public static String readInputStream(final InputStream content) {
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
      System.out.printf("InputStream exception message=%s", ex.getMessage());
    }
    return "";
  }
}
