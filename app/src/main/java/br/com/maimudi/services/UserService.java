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
import br.com.maimudi.model.User;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class UserService {
    private static final String TAG = UserService.class.getCanonicalName();

    public static void getUser(final Context context, final OnReadUser listenerUser,String user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(context.getResources().getString(R.string.ref_firebase_users));
        ref.orderByChild("nome").startAt(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<User> users = new ArrayList<>();
                if (dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()) {
                        if (!data.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            User u = new User();
                            u.setUid(data.getKey());
                            u.setNickname(data.child(context.getResources().getString(R.string.friends_nickname)).getValue().toString());
                            if (data.child(context.getResources().getString(R.string.friends_one_signal)).getValue() != null)
                                u.setOne_signal_id(data.child(context.getResources().getString(R.string.friends_one_signal)).getValue().toString());
                            u.setNome(data.child(context.getResources().getString(R.string.friends_nome)).getValue().toString());
                            if (data.child(context.getResources().getString(R.string.friends_profile_img)).getValue() != null){
                                u.setProfile_img(data.child(context.getResources().getString(R.string.friends_profile_img)).getValue().toString());
                            }
                            users.add(u);
                        }
                    }
                    listenerUser.onFinishedReadUsers(users);

                } else {
                    listenerUser.onFinishedReadUsers(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Falha ao recuperar usuários do firebase. Erro="+databaseError.getMessage());
                listenerUser.onFinishedReadUsers(null);
            }
        });
    }

    public interface OnReadUser extends Serializable{
        void onFinishedReadUsers(List<User> users);
    }
}
