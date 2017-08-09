package br.com.maimudi.services;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by ronan on 07/09/16.
 */
public interface DiscogsApi {
    @Headers({
            "Content-type: application/x-www-form-urlencoded",
            "Accept: application/vnd.discogs.v2.plaintext+json",
            "User-Agent: MaiMudiBrowser/0.1 +http://www.br4dev.com",
            "Authorization: Discogs token=dTwQfufbdyCiqPgSrdcKNRNNuTrqQIaJeAowyYZK"})
    @GET("search")
    Call<JsonElement> getInfoGrafico(@Query("track")String track, @Query("artist")String artist,
                                     @Query("per_page")int qtdPag, @Query("page")int page);
}
