package is.hello.gaibu.core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


public class ExpansionData {

    @JsonProperty("id")
    public final Long id;

    @JsonProperty("app_id")
    public final Long appId;

    @JsonProperty("device_id")
    public final String deviceId;

    @JsonProperty("data")
    public final String data;

    @JsonProperty("created_at")
    public final DateTime created;

    @JsonProperty("updated_at")
    public final DateTime updated;

    @JsonProperty("enabled")
    public final Boolean enabled;

    @JsonIgnore
    public final Optional<Long> accountId;

    public ExpansionData(
            final Long id,
            final Long appId,
            final String deviceId,
            final String data,
            final DateTime created,
            final DateTime updated,
            final Boolean enabled,
            final Long accountId
    ) {
        this.id = id;
        this.appId = appId;
        this.deviceId = deviceId;
        this.data = data;
        this.created = created;
        this.updated = updated;
        this.enabled = enabled;
        this.accountId = Optional.fromNullable(accountId);
    }

    public static class Builder {
        private Long id;
        private Long appId;
        private String deviceId;
        private String data;
        private DateTime created;
        private DateTime updated;
        private Boolean enabled;
        private Long accountId;

        public Builder() {
            created = DateTime.now(DateTimeZone.UTC);
            updated = DateTime.now(DateTimeZone.UTC);
        }

        public Builder withCreated(final DateTime created) {
            this.created = created;
            return this;
        }

        public Builder withDeviceId(final String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder withData(final String data) {
            this.data = data;
            return this;
        }

        public Builder withAppId(final Long appId) {
            this.appId = appId;
            return this;
        }

        public Builder withEnabled(final Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder withAccountId(final Long accountId) {
            this.accountId = accountId;
            return this;
        }

        public ExpansionData build() {
            return new ExpansionData(id, appId, deviceId, data, created, updated, enabled, accountId);
        }
    }
}
