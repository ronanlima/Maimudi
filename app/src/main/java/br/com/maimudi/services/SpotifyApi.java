package br.com.maimudi.services;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ronan Lima on 29/08/2016.
 */
public interface SpotifyApi {

    @GET("search")
    Call<JsonObject> getTracksSpotify(@Query("q") String query, @Query("country")String country,
                                      @Query("limit")int limit, @Query("type")String type, @Query("type")String type2
                                    , @Query("type")String type3);
}
