package is.hello.gaibu.weather.core.darksky;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Summary {
    final String summary;

    private Summary(String summary) {
        this.summary = summary;
    }

    public String summary() {
        return summary;
    }

    @JsonCreator
    public static Summary create(@JsonProperty("summary") String summary) {
        return new Summary(summary);
    }
}
