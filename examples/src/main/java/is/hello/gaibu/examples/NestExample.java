package is.hello.gaibu.examples;

import is.hello.gaibu.homeauto.clients.NestThermostat;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class NestExample {

    public static void main(String[] args) throws IOException {

        final OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "code=XMA6NMP5&client_id=6771b977-8034-4b0d-8524-e93d57ad69a5&client_secret=Aka1FEzqXkjAC0zgafg6kO9U7&grant_type=authorization_code");
        /*
        Request request = new Request.Builder()
                .url("https://api.home.nest.com/oauth2/access_token")
                .post(body)
                .build();
        /*
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
        */

        final NestThermostat nest = NestThermostat.create(
                "c.H0KtmAslFsQDSQ9Cydm2cR7UoB8XMJPIUNWd597h1Tr2XJuvjkVxIKNHjqTUfk2J9te8affgPO56gMzyyikgMpJtzVIgrjvqnVuc144gbEbftInw0LOsVuqCax1xw4VdNg0t2Fsn4tQeVBe0"
        );

        final Request request = new Request.Builder()
                .url("https://api.home.nest.com/oauth2/access_tokens/c.H0KtmAslFsQDSQ9Cydm2cR7UoB8XMJPIUNWd597h1Tr2XJuvjkVxIKNHjqTUfk2J9te8affgPO56gMzyyikgMpJtzVIgrjvqnVuc144gbEbftInw0LOsVuqCax1xw4VdNg0t2Fsn4tQeVBe0")
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("HTTP: " + response.code());
        /*
        {
            "access_token":"c.H0KtmAslFsQDSQ9Cydm2cR7UoB8XMJPIUNWd597h1Tr2XJuvjkVxIKNHjqTUfk2J9te8affgPO56gMzyyikgMpJtzVIgrjvqnVuc144gbEbftInw0LOsVuqCax1xw4VdNg0t2Fsn4tQeVBe0",
            "expires_in":315360000
        }

        */
    }
}
