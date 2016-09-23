package is.hello.gaibu.weather.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import is.hello.gaibu.weather.core.DarkSkyResponse;
import is.hello.gaibu.weather.interfaces.WeatherReport;
import is.hello.gaibu.weather.services.DarkSkyService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class DarkSky implements WeatherReport {


    final DarkSkyService service;

    public DarkSky(DarkSkyService service) {
        this.service = service;
    }

    public static DarkSky create() {
        return create("api-key-here", "https://api.darksky.net/forecast/");
    }

    public static DarkSky create(String apiKey, String baseUrl) {
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
        return new DarkSky(service);
    }


    public String get(float lat, float lon, long accountId, String senseId) {
        final String query = String.format("%f,%f", lat, lon);
        final Call<DarkSkyResponse> call = service.get(query);

        try {
            final Response<DarkSkyResponse> response = call.execute();
            final DarkSkyResponse resp = response.body();
            return resp.daily().summary();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return "Unable to get the weather";

    }
}
