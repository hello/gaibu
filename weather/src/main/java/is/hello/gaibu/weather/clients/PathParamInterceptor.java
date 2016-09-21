package is.hello.gaibu.weather.clients;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

class PathParamInterceptor implements Interceptor {
    private final String mKey;
    private final String mValue;

    public PathParamInterceptor(String key, String value) {
        mKey = String.format("{%s}", key);
        mValue = value;
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();

        HttpUrl.Builder urlBuilder = originalRequest.url().newBuilder();
        List<String> segments = originalRequest.url().pathSegments();

        for (int i = 0; i < segments.size(); i++) {
            if (mKey.equalsIgnoreCase(segments.get(i))) {
                urlBuilder.setPathSegment(i, mValue);
            }
        }

        Request request = originalRequest.newBuilder()
                .url(urlBuilder.build())
                .build();
        return chain.proceed(request);
    }
}
