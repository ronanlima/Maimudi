package br.com.maimudi.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.model.Friend;
import br.com.maimudi.model.Profile;
import br.com.maimudi.model.Timeline;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class ProfileService {
    private static final String TAG = ProfileService.class.getCanonicalName().toUpperCase();
    public ValueEventListener listenerPosts, listenerComments, listenerLikes, listenerUsers;
    public DatabaseReference refComment, refLikes, refUsers;
    public Query refPosts;

    public ProfileService() {
    }

    public void getProfile(final Context context, final String idUser, final Friend friend, final OnProfileLoaded onProfileLoaded) {
        final Profile profile = new Profile();
        final List<Timeline> timeline = new ArrayList<>();

        Query queryPosts = FirebaseDatabase.getInstance().getReference().child(context.getResources().getString(R.string.ref_firebase_posts)).orderByChild("user").equalTo(friend.getUser());
        setRefPosts(queryPosts);
        setListenerPosts(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    timeline.add(UtilFirebaseServices.recebePostFirebase(context, data));
                }
                setRefComment(FirebaseDatabase.getInstance().getReference().child(context.getResources().getString(R.string.ref_firebase_comments)));
                setListenerComments(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot commentOfPost:dataSnapshot.getChildren()) {
                            Timeline t = getByPostId(commentOfPost.getKey(), timeline);
                            if (t != null){
                                Long qtd = commentOfPost.getChildrenCount();
                                t.setCommentsNumber(qtd.toString());

                                for (DataSnapshot commentEspecific:commentOfPost.getChildren()) {
                                    UtilFirebaseServices.recebeCommentOfPost(t, context, commentEspecific);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Falha ao recuperar quantidade de comentários dos posts do usuário '"+idUser+"'.\n"+databaseError.getMessage());
                    }
                });
                getRefComment().addValueEventListener(getListenerComments());

                setRefLikes(FirebaseDatabase.getInstance().getReference().child(context.getResources().getString(R.string.ref_firebase_likes)));
                setListenerLikes(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data3:dataSnapshot.getChildren()) {
                            Timeline t = getByPostId(data3.getKey(), timeline);
                            if (t != null){
                                Long l = data3.getChildrenCount();
                                t.setLikes(l.intValue());
                            }
                        }
                        if (friend.getQtdAmigos() == 0){
                            setRefUsers(FirebaseDatabase.getInstance().getReference().child(context.getResources().getString(R.string.ref_firebase_users)));
                            setListenerUsers(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Long l = dataSnapshot.getChildrenCount();
                                    setTotalizadoresProfile(--l, profile, timeline, friend);
                                    finishMethod(profile, timeline, onProfileLoaded);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "Falha ao recuperar quantidade de seguidores para o usuário '"+idUser+"'.\n"+databaseError.getMessage());
                                    setDadosProfile(profile, timeline, friend);
                                    finishMethod(profile, timeline, onProfileLoaded);
                                }
                            });
                            getRefUsers().addValueEventListener(getListenerUsers());
                        } else {
                            setDadosProfile(profile, timeline, friend);
                            finishMethod(profile, timeline, onProfileLoaded);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Falha ao recuperar quantidade de likes dos posts do usuário '"+idUser+"'.\n"+databaseError.getMessage());
                        setDadosProfile(profile, timeline, friend);
                        finishMethod(profile, timeline, onProfileLoaded);
                    }
                });
                getRefLikes().addValueEventListener(getListenerLikes());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        getRefPosts().addValueEventListener(getListenerPosts());
    }

    private static void finishMethod(Profile profile, List<Timeline> timeline, OnProfileLoaded onProfileLoaded) {
        profile.setTimeline(timeline);
        onProfileLoaded.onFinishReadProfile(profile);
    }

    private static void setDadosProfile(Profile profile, List<Timeline> timeline, Friend friend) {
        String nameUser = "";
        if (timeline.size() != 0){
            nameUser = timeline.get(0).getUser();
        } else if (friend != null){
            nameUser = friend.getNickname();
        }
        profile.setUser(nameUser);
        profile.setMudis(String.valueOf(timeline.size()));
        profile.setListening(friend != null ? String.valueOf(friend.getQtdAmigos()) : "0");
        profile.setListener(friend != null ? String.valueOf(friend.getQtdAmigos()) : "0");
    }

    private static void setTotalizadoresProfile(Long qtdUsers, Profile profile, List<Timeline> timeline, Friend friend){
        String nameUser = "";
        if (timeline.size() != 0){
            nameUser = timeline.get(0).getUser();
        } else if (friend != null){
            nameUser = friend.getNickname();
        }
        profile.setUser(nameUser);
        profile.setMudis(String.valueOf(timeline.size()));
        profile.setListener(String.valueOf(qtdUsers));
        profile.setListening(String.valueOf(qtdUsers));
    }

    private static Timeline getByPostId(String id, List<Timeline> lista){
        for (Timeline time:lista) {
            if (time.getId().equals(id)){
                return time;
            }
        }
        return null;
    }

    public interface OnProfileLoaded extends Serializable{
        void onFinishReadProfile(Profile profile);
    }

    public ValueEventListener getListenerPosts() { return listenerPosts; }

    public void setListenerPosts(ValueEventListener listenerPosts) { this.listenerPosts = listenerPosts; }

    public ValueEventListener getListenerComments() {
        return listenerComments;
    }

    public void setListenerComments(ValueEventListener listenerComments) { this.listenerComments = listenerComments; }

    public ValueEventListener getListenerLikes() {
        return listenerLikes;
    }

    public void setListenerLikes(ValueEventListener listenerLikes) { this.listenerLikes = listenerLikes; }

    public ValueEventListener getListenerUsers() {
        return listenerUsers;
    }

    public void setListenerUsers(ValueEventListener listenerUsers) { this.listenerUsers = listenerUsers; }

    public Query getRefPosts() { return refPosts; }

    public void setRefPosts(Query refPosts) { this.refPosts = refPosts; }

    public DatabaseReference getRefComment() {
        return refComment;
    }

    public void setRefComment(DatabaseReference refComment) {
        this.refComment = refComment;
    }

    public DatabaseReference getRefLikes() {
        return refLikes;
    }

    public void setRefLikes(DatabaseReference refLikes) {
        this.refLikes = refLikes;
    }

    public DatabaseReference getRefUsers() {
        return refUsers;
    }

    public void setRefUsers(DatabaseReference refUsers) {
        this.refUsers = refUsers;
    }
}
