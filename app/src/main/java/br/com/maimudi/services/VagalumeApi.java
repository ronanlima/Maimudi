package br.com.maimudi.services;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ronan Lima on 31/08/2016.
 */
public interface VagalumeApi {

    @GET("search.php")
    Call<JsonObject> getLetra(@Query("art")String artista, @Query("mus")String musica, @Query("apikey")String apiKey);
}
