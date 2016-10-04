package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by jnorgan on 9/28/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HueScene {

  @JsonProperty("id")
  public final String id;

  @JsonProperty("name")
  public final String name;

  @JsonProperty("lights")
  public final String[] lights;

  @JsonProperty("owner")
  public final String owner;

  @JsonProperty("recycle")
  public final Boolean recycle;

  @JsonProperty("locked")
  public final Boolean locked;

  @JsonProperty("appdata")
  public final HueSceneAppData appData;

  @JsonProperty("picture")
  public final String picture;

  @JsonProperty("updated")
  public final Date updated;

  @JsonProperty("version")
  public final Integer version;

  public HueScene(
      final String id,
      final String name,
      final String[] lights,
      final String owner,
      final Boolean recycle,
      final Boolean locked,
      final HueSceneAppData appData,
      final String picture,
      final Date updated,
      final Integer version
  ) {
    this.id = id;
    this.name = name;
    this.lights = lights;
    this.owner = owner;
    this.recycle = recycle;
    this.locked = locked;
    this.appData = appData;
    this.picture = picture;
    this.updated = updated;
    this.version = version;
  }

  public static class Builder {
    private String id;
    private String name;
    private String[] lights;
    private String owner;
    private Boolean recycle;
    private Boolean locked;
    private HueSceneAppData appData;
    private String picture;
    private Date updated;
    private Integer version;

    public Builder() {
    }

    public Builder withId(final String id) {
      this.id = id;
      return this;
    }

    public Builder withName(final String name) {
      this.name = name;
      return this;
    }

    public Builder withLights(final String[] lights) {
      this.lights = lights;
      return this;
    }

    public Builder withOwner(final String owner) {
      this.owner = owner;
      return this;
    }

    public Builder withRecycle(final Boolean recycle) {
      this.recycle = recycle;
      return this;
    }

    public Builder withLocked(final Boolean locked) {
      this.locked = locked;
      return this;
    }

    public Builder withAppData(final HueSceneAppData appData) {
      this.appData = appData;
      return this;
    }

    public Builder withPicture(final String picture) {
      this.picture = picture;
      return this;
    }

    public Builder withUpdated(final Date updated) {
      this.updated = updated;
      return this;
    }

    public Builder withVersion(final Integer version) {
      this.version = version;
      return this;
    }

    public HueScene build() {
      return new HueScene(id, name, lights, owner, recycle, locked, appData, picture, updated, version);
    }
  }
}
