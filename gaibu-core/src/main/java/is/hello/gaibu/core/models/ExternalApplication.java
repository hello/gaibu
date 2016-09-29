package is.hello.gaibu.core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

public class ExternalApplication {

    @JsonProperty("id")
    public final Long id;

    @JsonProperty("service_name")
    public final String serviceName;

    @JsonProperty("device_name")
    public final String deviceName;

    @JsonProperty("client_id")
    public final String clientId;

    @JsonProperty("client_secret")
    public final String clientSecret;

    @JsonProperty("api_uri")
    public final String apiURI;

    @JsonProperty("auth_uri")
    public final String authURI;

    @JsonProperty("token_uri")
    public final String tokenURI;

    @JsonProperty("refresh_uri")
    public final String refreshURI;

    @JsonProperty("description")
    public final String description;

    @JsonProperty("created")
    public final DateTime created;

    @JsonIgnore
    public final Integer grantType;

    @JsonProperty("category")
    public final Category category;

    public ExternalApplication(
            final Long id,
            final String serviceName,
            final String deviceName,
            final String clientId,
            final String clientSecret,
            final String apiURI,
            final String authURI,
            final String tokenURI,
            final String refreshURI,
            final String description,
            final DateTime created,
            final Integer grantType,
            final Category category
    ) {
        this.id = id;
        this.serviceName = serviceName;
        this.deviceName = deviceName;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.apiURI = apiURI;
        this.authURI = authURI;
        this.tokenURI = tokenURI;
        this.refreshURI = refreshURI;
        this.description = description;
        this.created = created;
        this.grantType = grantType;
        this.category = category;
    }

    public enum Category {
        LIGHT("light"),
        MUSIC("music"),
        NEWS("news"),
        TEMPERATURE("temperature"),
        TRIVIA("trivia"),
        WEATHER("weather");


        private String value;

        Category(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static Category fromString(final String text) {
            if (text != null) {
                for (final Category cat : Category.values()) {
                    if (text.equalsIgnoreCase(cat.toString())) {
                        return cat;
                    }
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
