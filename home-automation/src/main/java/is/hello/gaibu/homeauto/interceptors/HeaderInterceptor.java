package is.hello.gaibu.homeauto.interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private final String token;

    public HeaderInterceptor(String token) {
        this.token = token;
    }

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder builder = request.newBuilder();
        builder.header("Accept", "application/json");
        builder.header("Authorization", String.format("Bearer %s", token));

        request = builder.build();
        return chain.proceed(request);
    }
}
