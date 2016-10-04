package is.hello.gaibu.weather.interfaces;

import java.net.InetAddress;

public interface WeatherReport {

    String get(double lat, double lon, long accountId, String senseId);
    String get(long accountId, String senseId, InetAddress ipAddress);
}
