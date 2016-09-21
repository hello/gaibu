package is.hello.gaibu.weather.interfaces;

public interface WeatherReport {

    String get(float lat, float lon, long accountId, String senseId);
}
