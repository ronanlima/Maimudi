package br.com.maimudi.services;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.model.Timeline;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class TimelineService {

    public static void getTimeline(final Context context, final List<Timeline> timeline,
                                   final OnReadListenerTimeline listenerTimeline){
        Query ref = FirebaseDatabase.getInstance().getReference().child(context.getResources().getString(R.string.ref_firebase_posts)).orderByChild("timestamp");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    timeline.add(UtilFirebaseServices.recebePostFirebase(context, data));
                }
                DatabaseReference refC = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.ref_firebase_comments));
                refC.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot commentOfPost:dataSnapshot.getChildren()) {
                            Timeline t = getByPostId(commentOfPost.getKey(), timeline);
                            if (t != null){
                                t.getComments().clear();
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

                    }
                });
                DatabaseReference refLikes = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.ref_firebase_likes));
                refLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data3:dataSnapshot.getChildren()) {
                            Timeline t = getByPostId(data3.getKey(), timeline);
                            if (t != null){
                                Long l = data3.getChildrenCount();
                                t.setLikes(l.intValue());
                                for (DataSnapshot data:data3.getChildren()) {
                                    if (data.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        t.setCantandoJunto(true);
                                        break;
                                    }
                                }
                            }
                        }
                        listenerTimeline.onFinishReadTimeline(timeline);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static Timeline getByPostId(String id, List<Timeline> lista){
        for (Timeline time:lista) {
            if (time.getId().equals(id)){
                return time;
            }
        }
        return null;
    }

    public interface OnReadListenerTimeline extends Serializable {
        void onFinishReadTimeline(List<Timeline> listaTimeline);
    }
}
