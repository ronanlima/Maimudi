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
import br.com.maimudi.model.Comment;
import br.com.maimudi.model.Timeline;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class CommentsService {
    private static final String TAG = CommentsService.class.getCanonicalName().toUpperCase();

    public static void getComment(final Context context, final Timeline timeline, final OnReadListenerComments listenerComments) {
        final List<Comment> comments = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.ref_firebase_comments));
        ref.child(timeline.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    Comment c = new Comment();
                    c.setUser(data.child(context.getResources().getString(R.string.comment_user)).getValue().toString());
                    c.setComment(data.child(context.getResources().getString(R.string.comment)).getValue().toString());
                    c.setNickname(data.child(context.getResources().getString(R.string.comment_nickname)).getValue().toString());
                    if (data.child(context.getResources().getString(R.string.comment_user_img)).getValue() != null){
                        c.setUser_img(data.child(context.getResources().getString(R.string.comment_user_img)).getValue().toString());
                    }
                    comments.add(c);
                }
                listenerComments.onFinishReadComments(comments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"Falha ao recuperar a lista de comentarios para o post '"+timeline.getId()+"'\n"+databaseError.getMessage());
                listenerComments.onFinishReadComments(comments);
            }
        });
    }

    public interface OnReadListenerComments extends Serializable{
        void onFinishReadComments(List<Comment> comments);
    }
}
