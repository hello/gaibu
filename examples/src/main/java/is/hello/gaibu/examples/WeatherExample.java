package is.hello.gaibu.examples;

import is.hello.gaibu.weather.clients.DarkSky;
import is.hello.gaibu.weather.interfaces.WeatherReport;

public class WeatherExample {

    public static void main(String[] args) {

        final WeatherReport report = DarkSky.create();
        System.out.println(report.get(37.773972f, -122.431297f, 99L, "sense"));

    }
}
