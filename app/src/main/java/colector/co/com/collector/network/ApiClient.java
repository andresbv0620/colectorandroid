package colector.co.com.collector.network;

import android.content.Context;
import com.squareup.otto.Bus;
import java.io.IOException;
import colector.co.com.collector.utils.PrefsUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jose Rodriguez on 11/06/2016.
 */
public class ApiClient {

    private static final String BASE_URL = "http://52.88.209.73:9000/";
    private static ApiClient mApiClient;
    private static Retrofit retrofitAdapter;
    private static HttpLoggingInterceptor interceptor;
    private static OkHttpClient client;
    private Bus mBus = BusProvider.getBus();

    public static ApiClient getInstance(Context mContext) {
        if (mApiClient == null)
            mApiClient = new ApiClient(mContext);
        return mApiClient;
    }

    private ApiClient(Context mContext) {

        interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().addInterceptor(interceptor);

        Interceptor agentInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder();

                if (getActiveAccountAuthToken() != null) {
                    requestBuilder.addHeader("Authorization", getActiveAccountAuthToken());
                }
                return chain.proceed(requestBuilder.build());
            }
        };

        clientBuilder.addInterceptor(agentInterceptor);
        client = clientBuilder.build();
        retrofitAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public String getActiveAccountAuthToken() {
        try {
            String token = PrefsUtils.getInstance().getPrefs().getString(PrefsUtils.STRING_ACTIVE_OAUTH_TOKEN, null);

            if (token != null && !token.isEmpty()) {
                StringBuilder tokenSB = new StringBuilder();
                tokenSB.append("Token");
                tokenSB.append(" ");
                tokenSB.append(token);
                return tokenSB.toString();
            } else return null;
        } catch (RuntimeException e){
            return null;
        }
    }

    public static Retrofit getRetrofitAdapter() {
        return retrofitAdapter;
    }

    public static void setRetrofitAdapter(Retrofit retrofitAdapter) {
        ApiClient.retrofitAdapter = retrofitAdapter;
    }
}