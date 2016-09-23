package is.hello.gaibu.weather.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import is.hello.gaibu.weather.core.darksky.Summary;

public class DarkSkyResponse {
    final Summary daily;

    private DarkSkyResponse(Summary daily) {
        this.daily = daily;
    }

    @JsonCreator
    public static DarkSkyResponse create(@JsonProperty("daily") Summary daily) {
        return new DarkSkyResponse(daily);
    }

    public Summary daily() {
        return daily;
    }
}
