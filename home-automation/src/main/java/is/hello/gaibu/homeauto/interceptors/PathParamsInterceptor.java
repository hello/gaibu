package is.hello.gaibu.homeauto.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class PathParamsInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PathParamsInterceptor.class);
    private final Map<String, String> params;

    public PathParamsInterceptor(final Map<String, String> params) {
        this.params = params;
    }

    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        HttpUrl.Builder urlBuilder = originalRequest.url().newBuilder();
        List<String> segments = originalRequest.url().pathSegments();

        for(final Map.Entry<String, String> entry : params.entrySet()){
            final String segmentKey = String.format("{%s}", entry.getKey());
            for (int i = 0; i < segments.size(); i++) {
                if (segmentKey.equalsIgnoreCase(segments.get(i))) {
                    urlBuilder.setPathSegment(i, entry.getValue());
                    if(entry.getValue().isEmpty()) {
                        LOGGER.warn("warn=empty-path-param param_name={}", entry.getKey());
                    }
                }
            }
        }

        Request request = originalRequest.newBuilder()
                .url(urlBuilder.build())
                .build();
        return chain.proceed(request);
    }
}
