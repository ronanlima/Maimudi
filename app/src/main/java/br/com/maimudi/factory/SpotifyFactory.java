package br.com.maimudi.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import br.com.maimudi.services.SpotifyApi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ronan Lima on 29/08/2016.
 */
public class SpotifyFactory {
    private static SpotifyFactory instance;
    private static String BASE_URL = "https://api.spotify.com/v1/";

    private SpotifyApi spotifyApi;

    private SpotifyFactory(){
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

        this.spotifyApi = retrofit.create(SpotifyApi.class);
    }

    public static synchronized SpotifyFactory getInstance(){
        if (instance == null) {
            instance = new SpotifyFactory();
        }
        return instance;
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    public void setSpotifyApi(SpotifyApi spotifyApi) {
        spotifyApi = spotifyApi;
    }
}
