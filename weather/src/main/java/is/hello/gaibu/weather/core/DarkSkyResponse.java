package is.hello.gaibu.weather.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import is.hello.gaibu.weather.core.darksky.Summary;

public class DarkSkyResponse {
    final Summary hourly;

    private DarkSkyResponse(Summary hourly) {
        this.hourly = hourly;
    }

    @JsonCreator
    public static DarkSkyResponse create(@JsonProperty("hourly") Summary hourly) {
        return new DarkSkyResponse(hourly);
    }

    public Summary hourly() {
        return hourly;
    }
}
