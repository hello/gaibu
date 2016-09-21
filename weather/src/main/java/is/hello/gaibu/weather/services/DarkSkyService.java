package is.hello.gaibu.weather.services;

import is.hello.gaibu.weather.core.DarkSkyResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DarkSkyService {

    @GET("/forecast/{api-key}/{query}")
    Call<DarkSkyResponse> get(@Path("query") String query);
}
