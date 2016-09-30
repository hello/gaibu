package is.hello.gaibu.core.models;

import com.google.common.base.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by km on 11/18/15.
 */
public class MultiDensityImage {

    @JsonIgnore
    public final String baseUrl;

    private final Optional<String> phoneDensityNormal;
    private final Optional<String> phoneDensityHigh;
    private final Optional<String> phoneDensityExtraHigh;

    @JsonProperty("phone_1x")
    public final Optional<String> phoneDensityNormal() {
        return formatUrl(phoneDensityNormal);
    };

    @JsonProperty("phone_2x")
    public final Optional<String> phoneDensityHigh(){
        return formatUrl(phoneDensityHigh);
    };

    @JsonProperty("phone_3x")
    public final Optional<String> phoneDensityExtraHigh() {
        return formatUrl(phoneDensityExtraHigh);
    };

    @JsonCreator
    public MultiDensityImage(@JsonProperty("phone_1x") final String phoneDensityNormal,
                             @JsonProperty("phone_2x") final String phoneDensityHigh,
                             @JsonProperty("phone_3x") final String phoneDensityExtraHigh) {
        this("", Optional.of(phoneDensityNormal), Optional.of(phoneDensityHigh), Optional.of(phoneDensityExtraHigh));
    }

    public MultiDensityImage(final Optional<String> phoneDensityNormal,
                             final Optional<String> phoneDensityHigh,
                             final Optional<String> phoneDensityExtraHigh) {
        this("", phoneDensityNormal, phoneDensityHigh, phoneDensityExtraHigh);
    }

    private MultiDensityImage(final String baseUrl, final Optional<String> phoneDensityNormal,
                              final Optional<String> phoneDensityHigh,
                              final Optional<String> phoneDensityExtraHigh) {
        this.baseUrl = baseUrl;
        this.phoneDensityNormal = phoneDensityNormal;
        this.phoneDensityHigh = phoneDensityHigh;
        this.phoneDensityExtraHigh = phoneDensityExtraHigh;
    }

    public static MultiDensityImage create(final String baseUrl,
                                           final Optional<String> phoneDensityNormal,
                                           final Optional<String> phoneDensityHigh,
                                           final Optional<String> phoneDensityExtraHigh) {
        return new MultiDensityImage(baseUrl, phoneDensityNormal, phoneDensityHigh, phoneDensityExtraHigh);
    }

    public static MultiDensityImage empty(){
        return new MultiDensityImage(Optional.<String>absent(),Optional.<String>absent(),Optional.<String>absent());
    }

    private Optional<String> formatUrl(final Optional<String> url) {
        if(!url.isPresent()) {
            return url;
        }
        if(!baseUrl.isEmpty()) {
            return Optional.of(baseUrl + url.get());
        }
        return url;
    }
}
