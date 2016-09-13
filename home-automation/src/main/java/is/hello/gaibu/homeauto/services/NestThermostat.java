package is.hello.gaibu.homeauto.services;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import is.hello.gaibu.homeauto.interfaces.ControllableThermostat;


/**
 * Created by jnorgan on 8/24/16.
 */
public class NestThermostat implements ControllableThermostat{
  private static final Logger LOGGER = LoggerFactory.getLogger(NestThermostat.class);

  private String accessToken;
  private String nestDeviceId;
  private String apiPath;
  private String apiEndpoint;
  private final Gson gson = new Gson();

  public static String DEFAULT_API_PATH = "https://developer-api.nest.com/";
  public static String DEFAULT_API_ENDPOINT = "devices/thermostats";


  public NestThermostat(final String nestDeviceId, final String apiPath, final String accessToken) {
    this.apiPath = apiPath;
    this.nestDeviceId = nestDeviceId;
    this.accessToken = accessToken;
    this.apiEndpoint = DEFAULT_API_ENDPOINT + "/" + nestDeviceId;
  }

  public void setStateValue(final String stateName, final Object stateValue) {
    final Map<String, Object> data = Maps.newHashMap(ImmutableMap.of(stateName, stateValue));
    putData(apiPath + apiEndpoint, accessToken, gson.toJson(data));
  }

  public void setTargetTemperature(final Integer temp) {
    setStateValue("target_temperature_f", temp);
  }

  public Integer getTemperature() {

    final Optional<String> optionalResult = getData("", accessToken);
    if(!optionalResult.isPresent()) {
      return 0;
    }

    final String resultJSON = optionalResult.get();
    LOGGER.debug("result={}", resultJSON);

    return 1;
  }

  public static String getThermostats(final String accessToken) {
    final Optional<String> response = getData(DEFAULT_API_PATH + DEFAULT_API_ENDPOINT, accessToken);
    if(!response.isPresent()) {
      LOGGER.error("error=get-thermostats-failed");
    }

    return response.get();
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
      LOGGER.error("error=get-request-failed message={}", ex.getMessage());
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
      connection.setInstanceFollowRedirects(true);

      final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
      out.write(data);
      out.close();


      final String result = readInputStream((InputStream)connection.getContent());

      return Optional.of(result);

    } catch (Exception ex) {
      LOGGER.error("error=put-request-failed message={}", ex.getMessage());
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
      LOGGER.error("error=inputstream-exception message={}", ex.getMessage());
    }
    return "";
  }

}
