package is.hello.gaibu.examples;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.google.common.collect.Lists;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import is.hello.gaibu.weather.clients.DarkSky;
import is.hello.gaibu.weather.interfaces.WeatherReport;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class WeatherExample {

    public static void main(String[] args) throws IOException, GeoIp2Exception {

        final File database = new File("/tmp/GeoLite2-City.mmdb");

        if(!database.exists()) {
            System.out.println("Fetching from S3");
            final AmazonS3 s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
            s3.getObject(new GetObjectRequest("hello-dev", "GeoLite2-City.mmdb"), database);
        }

        // This creates the DatabaseReader object, which should be reused across
        // lookups.
        final DatabaseReader reader = new DatabaseReader.Builder(database).build();
        final WeatherReport report = DarkSky.create(System.getenv("DARKSKY_KEY"), reader);

        final List<String> ipAddresses = Lists.newArrayList(
                "204.28.123.251",
                "74.71.60.187",
                "73.66.80.127",
                "47.137.19.72",
                "98.253.7.175"
        );

        DateTime now = DateTime.now(DateTimeZone.UTC);
        for(String ipAddress : ipAddresses) {
            System.out.println(report.get(99L, "sense", InetAddress.getByName(ipAddress)));
        }
        Seconds diff = Seconds.secondsBetween(now, DateTime.now(DateTimeZone.UTC));
        System.out.println("Took: " + diff.getSeconds());
    }
}
