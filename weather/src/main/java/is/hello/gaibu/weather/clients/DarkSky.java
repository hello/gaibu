package is.hello.gaibu.weather.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;
import is.hello.gaibu.weather.core.DarkSkyResponse;
import is.hello.gaibu.weather.interfaces.WeatherReport;
import is.hello.gaibu.weather.services.DarkSkyService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.InetAddress;

public class DarkSky implements WeatherReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(DarkSky.class);

    final DarkSkyService service;
    final DatabaseReader reader;

    private static String DEFAULT_ENDPOINT = "https://api.darksky.net/forecast/";

    public DarkSky(final DarkSkyService service, final DatabaseReader reader) {
        this.service = service;
        this.reader = reader;
    }

    public static DarkSky create(final String apiKey, final DatabaseReader reader) {
        return DarkSky.create(apiKey, DEFAULT_ENDPOINT, reader);
    }

    public static DarkSky create(final String apiKey, final String baseUrl, DatabaseReader reader) {
        final PathParamInterceptor pathParamInterceptor = new PathParamInterceptor("api-key", apiKey);
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(pathParamInterceptor)
                .build();

        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final JacksonConverterFactory converterFactory = JacksonConverterFactory.create(mapper);
        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(converterFactory)
                .client(client)
                .baseUrl(baseUrl)
                .build();

        final DarkSkyService service = retrofit.create(DarkSkyService.class);
        return new DarkSky(service, reader);
    }

    public String get(final long accountId, final String senseId, final InetAddress ipAddress) {
        try {
            final CityResponse city = reader.city(ipAddress);
            System.out.println(city.getCity().getName());
            final Location location = city.getLocation();
            return get(location.getLatitude(), location.getLongitude(), accountId, senseId);
        } catch (GeoIp2Exception e) {

        } catch (IOException e) {

        }

        return "Unable to get the weather. Try again.";
    }

    public String get(double lat, double lon, long accountId, String senseId) {
        final String query = String.format("%f,%f", lat, lon);
        final Call<DarkSkyResponse> call = service.get(query);

        try {
            final Response<DarkSkyResponse> response = call.execute();
            if(response.isSuccessful()) {
                final DarkSkyResponse resp = response.body();
                return resp.daily().summary();
            }
        } catch (IOException e) {
            LOGGER.error("error=get-weather account_id={} msg={}", accountId, e.getMessage());
        }

        LOGGER.error("error=get-weather account_id={} success=false", accountId);
        return "Unable to get the weather";
    }
}
