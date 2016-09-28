package is.hello.gaibu.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("id")
    public final Long id;

    @JsonProperty("category")
    public final ExternalApplication.Category category;

    @JsonProperty("device_name")
    public final String deviceName;

    @JsonProperty("service_name")
    public final ServiceName serviceName;

    @JsonProperty("icon_uri")
    public final String iconURI;

    @JsonProperty("auth_uri")
    public final String authURI;

    @JsonProperty("completion_uri")
    public final String completionURI;

    @JsonProperty("state")
    public final State state;

    @JsonProperty("created")
    public final DateTime created;

    public Expansion (
            final Long id,
            final ExternalApplication.Category category,
            final String deviceName,
            final ServiceName serviceName,
            final String iconURI,
            final String authURI,
            final String completionURI,
            final State state,
            final DateTime created

    ) {
        this.id = id;
        this.category = category;
        this.deviceName = deviceName;
        this.serviceName = serviceName;
        this.iconURI = iconURI;
        this.authURI = authURI;
        this.completionURI = completionURI;
        this.state = state;
        this.created = created;
    }

    public enum State {
        NOT_CONNECTED,  //No connection to the expansion has existed
        CONNECTED_ON,   //Expansion has been authorized/authenticated, and the user has enabled it in the app
        CONNECTED_OFF,  //Expansion has been authorized/authenticated, but the user has disabled it in the app
        REVOKED,        //User has requested the revocation of all credentials for the Expansion
        NOT_CONFIGURED  //Expansion is authenticated, but lacks required configuration information to function
    }

    public static class Builder {
        private Long id;
        private ExternalApplication.Category category;
        private String deviceName;
        private ServiceName serviceName;
        private String iconURI;
        private String authURI;
        private String completionURI;
        private State state;
        private DateTime created;

        public Builder() {
            created = DateTime.now(DateTimeZone.UTC);
        }

        public Builder withCreated(final DateTime created) {
            this.created = created;
            return this;
        }
        public Builder withCategory(final ExternalApplication.Category category) {
            this.category = category;
            return this;
        }

        public Builder withDeviceName(final String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public Builder withServiceName(final ServiceName serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder withIconURI(final String iconURI) {
            this.iconURI = iconURI;
            return this;
        }

        public Builder withAuthURI(final String authURI) {
            this.authURI = authURI;
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

        public Expansion build() {
            return new Expansion(id, category, deviceName, serviceName, iconURI, authURI, completionURI, state, created);
        }
    }
}