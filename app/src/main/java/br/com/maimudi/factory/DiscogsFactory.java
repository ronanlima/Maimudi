package br.com.maimudi.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import br.com.maimudi.services.DiscogsApi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ronan Lima on 29/08/2016.
 */
public class DiscogsFactory {
    private static DiscogsFactory instance;
    private static String BASE_URL = "https://api.discogs.com/database/";

    private DiscogsApi discogsApi;

    private DiscogsFactory(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder().header("Accept","Application/json")
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);
                return response;
            }
        });

        OkHttpClient client = okHttpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.discogsApi = retrofit.create(DiscogsApi.class);
    }

    public static synchronized DiscogsFactory getInstance(){
        if (instance == null) {
            instance = new DiscogsFactory();
        }
        return instance;
    }

    public DiscogsApi getDiscogsApi() { return discogsApi; }

    public void setDiscogsApi(DiscogsApi discogsApi) { this.discogsApi = discogsApi; }
}
