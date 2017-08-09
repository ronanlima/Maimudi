package br.com.maimudi.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
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
import br.com.maimudi.model.Post;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class FriendsService {
    private static final String TAG = FriendsService.class.getCanonicalName();

    public static void getFriends(final Context context, final OnReadFriends listenerFriends, final String status) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.ref_firebase_friends));
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("status").equalTo(status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Friend> friends = new ArrayList<>();
                if (dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()) {
                        if (!data.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Friend f = new Friend();
                            f.setUser(data.getKey());
                            f.setOne_signal_id(data.child(context.getResources().getString(R.string.friends_one_signal)).getValue().toString());
                            f.setStatus(data.child(context.getResources().getString(R.string.friends_status)).getValue().toString());
                            f.setNickname(data.child(context.getResources().getString(R.string.friends_nickname)).getValue().toString());
                            f.setNome(data.child(context.getResources().getString(R.string.friends_nome)).getValue().toString());
                            if (data.child(context.getResources().getString(R.string.friends_profile_img)).getValue() != null){
                                f.setImgUrl(data.child(context.getResources().getString(R.string.friends_profile_img)).getValue().toString());
                            }
                            friends.add(f);
                        }
                    }

                        final List<Post> posts = new ArrayList<>();
                        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.ref_firebase_posts));
                        refUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data:dataSnapshot.getChildren()) {
                                    posts.add(instanciaPost(data, context));
                                }
                                contabilizaQtdPostsByUser(friends, posts);
                                listenerFriends.onFinishedReadFriends(friends);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "Falha ao recuperar quantidade de posts de cada usuário. Erro="+databaseError.getMessage());
                                listenerFriends.onFinishedReadFriends(friends);
                            }
                        });

                } else {
                    listenerFriends.onFinishedReadFriends(friends);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Falha ao recuperar usuários do firebase. Erro="+databaseError.getMessage());
                listenerFriends.onFinishedReadFriends(null);
            }
        });
    }

    public static void getWaitFriends(final Context context, final OnReadFriends listenerFriends, final String status) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.ref_firebase_friends));
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("status").equalTo(status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Friend> friends = new ArrayList<>();
                if (dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()) {
                        if (!data.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Friend f = new Friend();
                            f.setUser(data.getKey());
                            f.setOne_signal_id(data.child(context.getResources().getString(R.string.friends_one_signal)).getValue().toString());
                            f.setStatus(data.child(context.getResources().getString(R.string.friends_status)).getValue().toString());
                            f.setNickname(data.child(context.getResources().getString(R.string.friends_nickname)).getValue().toString());
                            f.setNome(data.child(context.getResources().getString(R.string.friends_nome)).getValue().toString());
                            if (data.child(context.getResources().getString(R.string.friends_profile_img)).getValue() != null){
                                f.setImgUrl(data.child(context.getResources().getString(R.string.friends_profile_img)).getValue().toString());
                            }
                            friends.add(f);
                        }
                    }

                    listenerFriends.onFinishedReadWaitFriends(friends);

                } else {
                    listenerFriends.onFinishedReadWaitFriends(friends);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Falha ao recuperar usuários do firebase. Erro="+databaseError.getMessage());
                listenerFriends.onFinishedReadWaitFriends(null);
            }
        });
    }

    public static void getChooseFriends(final Context context, final OnReadFriends listenerFriends, final String status) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.ref_firebase_friends));
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("status").equalTo(status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Friend> friends = new ArrayList<>();
                if (dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()) {
                        if (!data.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Friend f = new Friend();
                            f.setUser(data.getKey());
                            f.setOne_signal_id(data.child(context.getResources().getString(R.string.friends_one_signal)).getValue().toString());
                            f.setStatus(data.child(context.getResources().getString(R.string.friends_status)).getValue().toString());
                            f.setNickname(data.child(context.getResources().getString(R.string.friends_nickname)).getValue().toString());
                            f.setNome(data.child(context.getResources().getString(R.string.friends_nome)).getValue().toString());
                            if (data.child(context.getResources().getString(R.string.friends_profile_img)).getValue() != null){
                                f.setImgUrl(data.child(context.getResources().getString(R.string.friends_profile_img)).getValue().toString());
                            }
                            friends.add(f);
                        }
                    }

                    listenerFriends.onFinishedReadChooseFriends(friends);

                } else {
                    listenerFriends.onFinishedReadChooseFriends(friends);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Falha ao recuperar usuários do firebase. Erro="+databaseError.getMessage());
                listenerFriends.onFinishedReadChooseFriends(null);
            }
        });
    }

    private static void contabilizaQtdPostsByUser(List<Friend> friends, List<Post> posts) {
        for (Friend f:friends) {
            for (Post p:posts) {
                if (p.getUser().equals(f.getUser())){
                    Integer qtd = f.getMudis() != null ? Integer.parseInt(f.getMudis())+1 : 1;
                    f.setMudis(qtd.toString());
                }
            }
        }
    }

    private static Post instanciaPost(DataSnapshot data, Context context) {
        Post p = new Post();
        p.setUser(data.child(context.getResources().getString(R.string.timeline_user)).getValue().toString());
        p.setId(data.getKey());
        return p;
    }

    public interface OnReadFriends extends Serializable{
        void onFinishedReadFriends(List<Friend> friends);
        void onFinishedReadWaitFriends(List<Friend> friends);
        void onFinishedReadChooseFriends(List<Friend> friends);
    }
}
