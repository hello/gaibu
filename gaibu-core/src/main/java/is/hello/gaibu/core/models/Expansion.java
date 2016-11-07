package is.hello.gaibu.core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hello.suripu.core.models.ValueRange;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Expansion
{
    public enum ServiceName {
        HUE("HUE"),
        NEST("NEST");

        private final String name;

        ServiceName(final String name) {
            this.name = name;
        }

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

    public enum State {
        NOT_CONNECTED,  //No connection to the expansion has existed
        CONNECTED_ON,   //Expansion has been authorized/authenticated, and the user has enabled it in the app
        CONNECTED_OFF,  //Expansion has been authorized/authenticated, but the user has disabled it in the app
        REVOKED,        //User has requested the revocation of all credentials for the Expansion
        NOT_CONFIGURED,  //Expansion is authenticated, but lacks required configuration information to function
        NOT_AVAILABLE // Expansion is not available. Controlled by feature flipper
    }


    @JsonProperty("id")
    public final Long id;

    @JsonProperty("service_name")
    public final ServiceName serviceName;

    @JsonProperty("device_name")
    public final String deviceName;

    @JsonProperty("company_name")
    public final String companyName;

    @JsonProperty("description")
    public final String description;

    @JsonProperty("icon")
    public final MultiDensityImage icon;

    @JsonProperty("client_id")
    @JsonIgnore
    public final String clientId;

    @JsonProperty("client_secret")
    @JsonIgnore
    public final String clientSecret;

    @JsonProperty("api_uri")
    @JsonIgnore
    public final String apiURI;

    @JsonProperty("auth_uri")
    public String authURI;

    @JsonProperty("token_uri")
    public final String tokenURI;

    @JsonProperty("refresh_uri")
    public final String refreshURI;

    @JsonProperty("category")
    public final Category category;

    @JsonProperty("created")
    public final DateTime created;

    @JsonProperty("grant_type")
    @JsonIgnore
    public final Integer grantType;

    @JsonProperty("completion_uri")
    public String completionURI;

    @JsonProperty("state")
    public Expansion.State state;

    @JsonProperty("value_range")
    public ValueRange valueRange;

    public Expansion (
            final Long id,
            final ServiceName serviceName,
            final String deviceName,
            final String companyName,
            final String description,
            final MultiDensityImage icon,
            final String clientId,
            final String clientSecret,
            final String apiURI,
            final String authURI,
            final String tokenURI,
            final String refreshURI,
            final Category category,
            final DateTime created,
            final Integer grantType,
            final String completionURI,
            final State state,
            final ValueRange valueRange

    ) {
        this.id = id;
        this.serviceName = serviceName;
        this.deviceName = deviceName;
        this.companyName = companyName;
        this.description = description;
        this.icon = icon;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.apiURI = apiURI;
        this.authURI = authURI;
        this.tokenURI = tokenURI;
        this.refreshURI = refreshURI;
        this.category = category;
        this.created = created;
        this.grantType = grantType;
        this.completionURI = completionURI;
        this.state = state;
        this.valueRange = valueRange;
    }

    public static class Builder {
        private Long id;
        private ServiceName serviceName;
        private String deviceName;
        private String companyName;
        private String description;
        private MultiDensityImage icon;
        private String clientId;
        private String clientSecret;
        private String apiURI;
        private String authURI;
        private String tokenURI;
        private String refreshURI;
        private Category category;
        private DateTime created;
        private Integer grantType;
        private String completionURI;
        private State state;
        private ValueRange valueRange;

        public Builder() {
            created = DateTime.now(DateTimeZone.UTC);
        }

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withServiceName(final ServiceName serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder withDeviceName(final String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public Builder withCompanyName(final String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder withIcon(final MultiDensityImage icon) {
            this.icon = icon;
            return this;
        }

        public Builder withClientId(final String clientId) {
            this.clientId = clientId;
            return this;
        }
        public Builder withClientSecret(final String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }
        public Builder withApiURI(final String apiURI) {
            this.apiURI = apiURI;
            return this;
        }
        public Builder withAuthURI(final String authURI) {
            this.authURI = authURI;
            return this;
        }

        public Builder withTokenURI(final String tokenURI) {
            this.tokenURI = tokenURI;
            return this;
        }

        public Builder withRefreshURI(final String refreshURI) {
            this.refreshURI = refreshURI;
            return this;
        }

        public Builder withCategory(final Category category) {
            this.category = category;
            return this;
        }

        public Builder withCreated(final DateTime created) {
            this.created = created;
            return this;
        }

        public Builder withGrantType(final Integer grantType) {
            this.grantType = grantType;
            return this;
        }

        public Builder withCompletionURI(final String completionURI) {
            this.completionURI = completionURI;
            return this;
        }

        public Builder withState(final State state) {
            this.state = state;
            return this;
        }

        public Builder withValueRange(final ValueRange valueRange) {
            this.valueRange = valueRange;
            return this;
        }

        public Expansion build() {
            return new Expansion(id, serviceName, deviceName, companyName, description, icon, clientId,
                clientSecret, apiURI, authURI, tokenURI, refreshURI, category, created, grantType,
                completionURI, state, valueRange);
        }
    }
}
