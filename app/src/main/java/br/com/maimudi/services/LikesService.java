package br.com.maimudi.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.model.Friend;
import br.com.maimudi.model.Timeline;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class LikesService {
    private static final String TAG = LikesService.class.getCanonicalName().toUpperCase();

    public static void getLikes(final Context context, final Timeline timeline, final OnReadListenerLikes listenerLikes) {
        final List<Friend> friends = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.ref_firebase_likes));
        ref.child(timeline.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot data:dataSnapshot.getChildren()) {
                    Friend f = new Friend();
                    f.setNickname(data.child(context.getResources().getString(R.string.friends_nickname)).getValue().toString());
                    if (data.child(context.getResources().getString(R.string.friends_profile_img)).getValue() != null) {
                        f.setImgUrl(data.child(context.getResources().getString(R.string.friends_profile_img)).getValue().toString());
                    }
                    friends.add(f);
                }
                listenerLikes.onFinishReadLikes(friends);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"Falha ao recuperar a lista de likes para o post '"+timeline.getId()+"'\n"+databaseError.getMessage());
                listenerLikes.onFinishReadLikes(friends);
            }
        });
    }

    public interface OnReadListenerLikes extends Serializable{
        void onFinishReadLikes(List<Friend> friends);
    }
}
