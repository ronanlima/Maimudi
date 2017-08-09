package br.com.maimudi.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.com.maimudi.R;

/**
 * Created by Ronan Lima on 09/09/2016.
 */
public class PostChartService {
    protected static final String TAG = PostChartService.class.getCanonicalName().toUpperCase();

    public static void getArtistsFromPosts(Context context, String idUser, final onFinishReadPosts listenerPosts){
        final Map<String, Integer> artists = new HashMap<>();
        final Map<String, Integer> genres = new HashMap<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(context.getResources().getString(R.string.ref_firebase_charts));
        reference.child(idUser).orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer sumArtists = 0, sumGenres = 0;
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChildren()){
                        for (DataSnapshot data:dataSnapshot.getChildren()) {
                            switch (data.getKey()){
                                case "artist":
                                    sumArtists = putElementsInMap(sumArtists, data, artists);
                                    break;
                                case "genre":
                                    sumGenres = putElementsInMap(sumGenres, data, genres);
                                    break;
                            }
                        }
                    }
                }
                listenerPosts.onFinishRead(artists, genres, sumArtists, sumGenres);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Falha ao recuperar os artistas para montagem do gráfico. Error = "+databaseError.getMessage());
                listenerPosts.onFinishRead(artists, genres, 0, 0);
            }
        });
    }

    private static int putElementsInMap(int sumMap, DataSnapshot data, Map<String, Integer> map) {
        for (DataSnapshot data2:data.getChildren()) {
            sumMap = sumMap + Integer.parseInt(data2.getValue().toString());
            map.put(data2.getKey(), Integer.parseInt(data2.getValue().toString()));
        }
        return sumMap;
    }

    public interface onFinishReadPosts extends Serializable {
        void onFinishRead(Map<String, Integer> artists, Map<String, Integer> genres, int sumArtists, int sumGenres);
    }
}
