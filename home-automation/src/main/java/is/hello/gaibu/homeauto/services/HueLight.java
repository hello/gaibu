package is.hello.gaibu.homeauto.services;

import com.google.common.base.Optional;

import com.philips.lighting.hue.sdk.clip.PHLightSerializer;
import com.philips.lighting.hue.sdk.clip.serialisation.PHLightSerializer1;
import com.philips.lighting.model.PHLightState;

import org.json.hue.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import is.hello.gaibu.homeauto.interfaces.ColoredLight;

/**
 * Created by jnorgan on 8/24/16.
 */
public class HueLight implements ColoredLight{

  public static String DEFAULT_BRIDGE_ID = "001788fffe2634fb";
  public static String DEFAULT_WHITELIST_ID = "Q9sGj7mJZfpyGuXXL9KB3-uMFn0BETY7U0Pto0ni";
  public static String DEFAULT_ACCESS_TOKEN = "KkWYVxG1OIMggw4fTiCEg6YWtoo4";
  public static String DEFAULT_API_PATH = "https://api.meethue.com/v2/bridges/";

  public HueLight() {}

  public void setColor() {

  }

  public void setBrightness(final int value) {
    //brightness is a unit8
    final int brightValue = value & 0xFF;
    final String data = "{\"bri\":" + brightValue + "}";
    putData("lights/1/state", data);
  }

  public void adjustBrightness(final Integer amount) {
    final String data = "{\"bri_inc\":" + amount.toString() + "}";
    putData("lights/1/state", data);
  }

  public PHLightState getLightState(final Integer lightId) {

    final String endpoint = "lights/" + lightId.toString();
    final Optional<String> response = getData(endpoint);
    if(!response.isPresent()) {
      return new PHLightState();
    }

    try {
      PHLightSerializer serializer = new PHLightSerializer1();
      return serializer.parseLightState(new JSONObject(response.get()).getJSONObject("state"));
    } catch (Exception ex) {
      System.out.printf("Json parsing issue: %s", ex.getMessage());
    }
    return new PHLightState();
  }

  public void setLightState(final Boolean isOn, final Integer transitionTime) {
    final String data = "{\"on\":" + isOn.toString() + ", \"transitiontime\":"+ transitionTime.toString() + "}";
    final Optional<String> response = putData("lights/1/state", data);

  }
  public void setLightState(final Boolean isOn){
    setLightState(isOn, 4);
  }

  public Optional<String> getData(final String endpoint) {
    try {
      final URL uri = new URL(DEFAULT_API_PATH + DEFAULT_BRIDGE_ID + "/" + DEFAULT_WHITELIST_ID + "/" + endpoint);
      final HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(8000);
      connection.setReadTimeout(8000);
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Bearer " + DEFAULT_ACCESS_TOKEN);
      connection.setRequestProperty("Content-Type", "application/json");
      connection.connect();

      if (connection.getResponseCode() == 200) {
        final String result = readInputStream((InputStream)connection.getContent());

        return Optional.of(result);
      }
      return Optional.absent();

    }catch (Exception ex) {
      System.out.printf("Connect Exception: %s", ex.getMessage());
    }

    return Optional.absent();
  }

  public Optional<String> putData(final String endpoint, final String data) {
    try {
      final URL uri = new URL(DEFAULT_API_PATH + DEFAULT_BRIDGE_ID + "/" + DEFAULT_WHITELIST_ID + "/" + endpoint);
      final HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
      connection.setRequestMethod("PUT");
      connection.setConnectTimeout(8000);
      connection.setReadTimeout(8000);
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Bearer " + DEFAULT_ACCESS_TOKEN);
      connection.setRequestProperty("Content-Type", "application/json");

      final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
      out.write(data);
      out.close();


      final String result = readInputStream((InputStream)connection.getContent());

      return Optional.of(result);

    } catch (Exception ex) {
      System.out.printf("bridge not connected %s", ex.getMessage());
    }

    return Optional.absent();
  }

  public String readInputStream(final InputStream content) {
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
