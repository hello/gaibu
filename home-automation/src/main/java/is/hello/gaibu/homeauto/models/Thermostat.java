package is.hello.gaibu.homeauto.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Thermostat{

  /**
   * Possible values for hvac_mode
   */
  public static enum HvacMode {
    HEAT("heat"),
    COOL("cool"),
    HEAT_COOL("heat-cool"),
    OFF("off"),
    ECO("eco");

    private final String mode;

    private HvacMode(String mode) {
      this.mode = mode;
    }

    @JsonValue
    public String value() {
      return mode;
    }

    @JsonCreator
    public static HvacMode forValue(String v) {
      for (HvacMode hm : HvacMode.values()) {
        if (hm.mode.equals(v)) {
          return hm;
        }
      }
      throw new IllegalArgumentException("Invalid hvac_mode: " + v);
    }

    @Override
    public String toString() {
      return this.mode;
    }
  }

  /**
   * Possible values for hvac_state
   */
  public static enum HvacState {
    HEATING("heating"),
    COOLING("cooling"),
    OFF("off");

    private final String state;

    private HvacState(String state) {
      this.state = state;
    }

    @JsonValue
    public String value() {
      return state;
    }

    @JsonCreator
    public static HvacState forValue(String v) {
      for (HvacState hs : HvacState.values()) {
        if (hs.state.equals(v)) {
          return hs;
        }
      }
      throw new IllegalArgumentException("Invalid hvac_state: " + v);
    }

    @Override
    public String toString() {
      return this.state;
    }
  }

  private Boolean can_cool;
  private Boolean can_heat;
  private Boolean is_using_emergency_heat;
  private Boolean has_fan;
  private Boolean fan_timer_active;
  private Date fan_timer_timeout;
  private Boolean has_leaf;
  private String temperature_scale;
  private BigDecimal target_temperature_f;
  private BigDecimal target_temperature_c;
  private BigDecimal target_temperature_high_f;
  private BigDecimal target_temperature_high_c;
  private BigDecimal target_temperature_low_f;
  private BigDecimal target_temperature_low_c;
  private BigDecimal away_temperature_high_f;
  private BigDecimal away_temperature_high_c;
  private BigDecimal away_temperature_low_f;
  private BigDecimal away_temperature_low_c;
  private HvacMode hvac_mode;
  private BigDecimal ambient_temperature_f;
  private BigDecimal ambient_temperature_c;
  private BigDecimal humidity;
  private HvacState hvac_state;
  private String where_id;
  private Boolean is_locked;
  private String locked_temp_min_f;
  private String locked_temp_max_f;
  private String locked_temp_min_c;
  private String locked_temp_max_c;
  private String label;
  private String name;
  private String structureId;

  /**
   * @return System ability to cool (AC)
   */
  @JsonProperty("can_cool")
  public Boolean getCan_cool() {
    return this.can_cool;
  }

  /**
   * @return System ability to heat
   */
  @JsonProperty("can_heat")
  public Boolean getCan_heat() {
    return this.can_heat;
  }

  /**
   * @return Emergency Heat status in systems with heat pumps
   */
  @JsonProperty("is_using_emergency_heat")
  public Boolean getIs_using_emergency_heat() {
    return this.is_using_emergency_heat;
  }

  /**
   * @return System ability to control the fan separately from heating or cooling
   */
  @JsonProperty("has_fan")
  public Boolean getHas_fan() {
    return this.has_fan;
  }

  /**
   * @return Indicates if the fan timer is engaged; used with 'fan_timer_timeout' to turn on the fan for a
   *         (user-specified) preset duration
   */
  @JsonProperty("fan_timer_active")
  public Boolean getFan_timer_active() {
    return this.fan_timer_active;
  }

  /**
   * Sets if the fan timer is engaged; used with 'fan_timer_timeout' to turn on the fan for a (user-specified) preset
   * duration
   */
  @JsonProperty("fan_timer_active")
  public void setFan_timer_active(Boolean fan_timer_active) {
    this.fan_timer_active = fan_timer_active;
  }

  /**
   * @return Timestamp, showing when the fan timer reaches 0 (end of timer duration)
   */
  @JsonProperty("fan_timer_timeout")
  public Date getFan_timer_timeout() {
    return this.fan_timer_timeout;
  }

  /**
   * @return Displayed when users choose an energy-saving temperature
   */
  @JsonProperty("has_leaf")
  public Boolean getHas_leaf() {
    return this.has_leaf;
  }

  /**
   * @return Celsius ("C") or Fahrenheit ("F"); used with temperature display
   */
  @JsonProperty("temperature_scale")
  public String getTemperature_scale() {
    return this.temperature_scale;
  }

  /**
   * Set the temperature scale to "C" or "F" for display on the thermostat
   */
  @JsonProperty("temperature_scale")
  public void setTemperature_scale(String temperature_scale) {
    this.temperature_scale = temperature_scale;
  }

  /**
   * @return Desired temperature, displayed in whole degrees Fahrenheit (1°F)
   */
  @JsonProperty("target_temperature_f")
  public BigDecimal getTarget_temperature_f() {
    return this.target_temperature_f;
  }

  /**
   * Desired temperature, displayed in whole degrees Fahrenheit (1°F)
   */
  @JsonProperty("target_temperature_f")
  public void setTarget_temperature_f(BigDecimal target_temperature_f) {
    this.target_temperature_f = target_temperature_f;
  }

  /**
   * @return Desired temperature, displayed in half degrees Celsius (0.5°C)
   */
  @JsonProperty("target_temperature_c")
  public BigDecimal getTarget_temperature_c() {
    return this.target_temperature_c;
  }

  /**
   * Desired temperature, displayed in half degrees Celsius (0.5°C)
   */
  @JsonProperty("target_temperature_c")
  public void setTarget_temperature_c(BigDecimal target_temperature_c) {
    this.target_temperature_c = target_temperature_c;
  }

  /**
   * @return Maximum target temperature, displayed in whole degrees Fahrenheit (1°F); used with Heat • Cool mode
   */
  @JsonProperty("target_temperature_high_f")
  public BigDecimal getTarget_temperature_high_f() {
    return this.target_temperature_high_f;
  }

  /**
   * Maximum target temperature, displayed in whole degrees Fahrenheit (1°F); used with Heat • Cool mode
   */
  @JsonProperty("target_temperature_high_f")
  public void setTarget_temperature_high_f(BigDecimal target_temperature_high_f) {
    this.target_temperature_high_f = target_temperature_high_f;
  }

  /**
   * @return Maximum target temperature, displayed in half degrees Celsius (0.5°C); used with Heat • Cool mode
   */
  @JsonProperty("target_temperature_high_c")
  public BigDecimal getTarget_temperature_high_c() {
    return this.target_temperature_high_c;
  }

  /**
   * Maximum target temperature, displayed in half degrees Celsius (0.5°C); used with Heat • Cool mode
   */
  @JsonProperty("target_temperature_high_c")
  public void setTarget_temperature_high_c(BigDecimal target_temperature_high_c) {
    this.target_temperature_high_c = target_temperature_high_c;
  }

  /**
   * @return Minimum target temperature, displayed in whole degrees Fahrenheit (1°F); used with Heat • Cool mode
   */
  @JsonProperty("target_temperature_low_f")
  public BigDecimal getTarget_temperature_low_f() {
    return this.target_temperature_low_f;
  }

  /**
   * Minimum target temperature, displayed in whole degrees Fahrenheit (1°F); used with Heat • Cool mode
   */
  @JsonProperty("target_temperature_low_f")
  public void setTarget_temperature_low_f(BigDecimal target_temperature_low_f) {
    this.target_temperature_low_f = target_temperature_low_f;
  }

  /**
   * @return Minimum target temperature, displayed in half degrees Celsius (0.5°C); used with Heat • Cool mode
   */
  @JsonProperty("target_temperature_low_c")
  public BigDecimal getTarget_temperature_low_c() {
    return this.target_temperature_low_c;
  }

  /**
   * Minimum target temperature, displayed in half degrees Celsius (0.5°C); used with Heat • Cool mode
   */
  @JsonProperty("target_temperature_low_c")
  public void setTarget_temperature_low_c(BigDecimal target_temperature_low_c) {
    this.target_temperature_low_c = target_temperature_low_c;
  }

  /**
   * @return Maximum 'away' temperature, displayed in whole degrees Fahrenheit (1°F)
   */
  @JsonProperty("away_temperature_high_f")
  public BigDecimal getAway_temperature_high_f() {
    return this.away_temperature_high_f;
  }

  /**
   * @return Maximum 'away' temperature, displayed in half degrees Celsius (0.5°C)
   */
  @JsonProperty("away_temperature_high_c")
  public BigDecimal getAway_temperature_high_c() {
    return this.away_temperature_high_c;
  }

  /**
   * @return Minimum 'away' temperature, displayed in whole degrees Fahrenheit (1°F)
   */
  @JsonProperty("away_temperature_low_f")
  public BigDecimal getAway_temperature_low_f() {
    return this.away_temperature_low_f;
  }

  /**
   * @return Minimum 'away' temperature, displayed in half degrees Celsius (0.5°C)
   */
  @JsonProperty("away_temperature_low_c")
  public BigDecimal getAway_temperature_low_c() {
    return this.away_temperature_low_c;
  }

  /**
   * @return Indicates HVAC system heating/cooling modes; for systems with both heating and cooling capability, use
   *         'heat-cool': (Heat • Cool mode)
   */
  @JsonProperty("hvac_mode")
  public HvacMode getHvac_mode() {
    return this.hvac_mode;
  }

  /**
   * Indicates HVAC system heating/cooling modes; for systems with both heating and cooling capability, use
   * 'heat-cool': (Heat • Cool mode)
   */
  @JsonProperty("hvac_mode")
  public void setHvac_mode(HvacMode hvac_mode) {
    this.hvac_mode = hvac_mode;
  }

  /**
   * @return Temperature, measured at the device, in whole degrees Fahrenheit (1°F)
   */
  @JsonProperty("ambient_temperature_f")
  public BigDecimal getAmbient_temperature_f() {
    return this.ambient_temperature_f;
  }

  /**
   * @return Temperature, measured at the device, in half degrees Celsius (0.5°C)
   */
  @JsonProperty("ambient_temperature_c")
  public BigDecimal getAmbient_temperature_c() {
    return this.ambient_temperature_c;
  }

  /**
   * @return Humidity, in percent (%) format, measured at the device.
   */
  @JsonProperty("humidity")
  public BigDecimal getHumidity() {
    return this.humidity;
  }

  /**
   * @return Indicates HVAC system heating/cooling/off state.
   */
  @JsonProperty("hvac_state")
  public HvacState getHvac_state() {
    return this.hvac_state;
  }

  /**
   * @return Where unique identifier.
   */
  @JsonProperty("where_id")
  public String getWhere_id() {
    return this.where_id;
  }

  /**
   * @return Thermostat Lock status. When true, the Thermostat Lock feature is enabled, and restricts the temperature
   *         range to these min/max values: locked_temp_min_f, locked_temp_max_f, locked_temp_min_c, and
   *         locked_temp_max_c.
   */
  @JsonProperty("is_locked")
  public Boolean getIs_locked() {
    return this.is_locked;
  }

  /**
   * @return Minimum Thermostat Lock temperature, displayed in whole degrees Fahrenheit (1°F). Used when is_locked is
   *         true.
   */
  @JsonProperty("locked_temp_min_f")
  public String getLocked_temp_min_f() {
    return this.locked_temp_min_f;
  }

  /**
   * @return Maximum Thermostat Lock temperature, displayed in whole degrees Fahrenheit (1°F). Used when is_locked is
   *         true.
   */
  @JsonProperty("locked_temp_max_f")
  public String getLocked_temp_max_f() {
    return this.locked_temp_max_f;
  }

  /**
   * @return Minimum Thermostat Lock temperature, displayed in half degrees Celsius (0.5°C). Used when is_locked is
   *         true.
   */
  @JsonProperty("locked_temp_min_c")
  public String getLocked_temp_min_c() {
    return this.locked_temp_min_c;
  }

  /**
   * @return Maximum Thermostat Lock temperature, displayed in half degrees Celsius (0.5°C). Used when is_locked is
   *         true.
   */
  @JsonProperty("locked_temp_max_c")
  public String getLocked_temp_max_c() {
    return this.locked_temp_max_c;
  }

  /**
   * Set the thermostat custom label.
   */
  @JsonProperty("label")
  public void setLabel(String label) {
    this.label = label;
  }

  @JsonProperty("name")
  public String getName() {
    return this.name;
  }

  @JsonProperty("structure_id")
  public String getStructureId() {
    return this.structureId;
  }

  @JsonProperty("structure_id")
  public void setStructureId(final String structureId) {
    this.structureId = structureId;
  }



//  @Override
//  public String toString() {
//    final ToStringBuilder builder = createToStringBuilder();
//    builder.appendSuper(super.toString());
//    builder.append("can_cool", this.can_cool);
//    builder.append("can_heat", this.can_heat);
//    builder.append("is_using_emergency_heat", this.is_using_emergency_heat);
//    builder.append("has_fan", this.has_fan);
//    builder.append("fan_timer_active", this.fan_timer_active);
//    builder.append("fan_timer_timeout", this.fan_timer_timeout);
//    builder.append("has_leaf", this.has_leaf);
//    builder.append("temperature_scale", this.temperature_scale);
//    builder.append("target_temperature_f", this.target_temperature_f);
//    builder.append("target_temperature_c", this.target_temperature_c);
//    builder.append("target_temperature_high_f", this.target_temperature_high_f);
//    builder.append("target_temperature_high_c", this.target_temperature_high_c);
//    builder.append("target_temperature_low_f", this.target_temperature_low_f);
//    builder.append("target_temperature_low_c", this.target_temperature_low_c);
//    builder.append("away_temperature_high_f", this.away_temperature_high_f);
//    builder.append("away_temperature_high_c", this.away_temperature_high_c);
//    builder.append("away_temperature_low_f", this.away_temperature_low_f);
//    builder.append("away_temperature_low_c", this.away_temperature_low_c);
//    builder.append("hvac_mode", this.hvac_mode);
//    builder.append("ambient_temperature_f", this.ambient_temperature_f);
//    builder.append("ambient_temperature_c", this.ambient_temperature_c);
//    builder.append("humidity", this.humidity);
//    builder.append("hvac_state", this.hvac_state);
//    builder.append("where_id", this.where_id);
//    builder.append("is_locked", this.is_locked);
//    builder.append("locked_temp_min_f", this.locked_temp_min_f);
//    builder.append("locked_temp_max_f", this.locked_temp_max_f);
//    builder.append("locked_temp_min_c", this.locked_temp_min_c);
//    builder.append("locked_temp_max_c", this.locked_temp_max_c);
//    builder.append("label", this.label);
//
//    return builder.toString();
//  }
}