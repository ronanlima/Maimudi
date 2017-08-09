package br.com.maimudi.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.activity.MainTab;
import br.com.maimudi.adapter.FindUserAdapter;
import br.com.maimudi.model.User;
import br.com.maimudi.services.UserService;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class FindUserFragments extends Fragment {

    protected RecyclerView recyclerView;
    private LinearLayout llEmptyFriends;
    private List<User> findUsers;
    private LinearLayoutManager mLayoutManager;
    private FindUserAdapter findUserAdapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_users, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        llEmptyFriends = (LinearLayout) view.findViewById(R.id.ll_empty_friends);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                taskUser(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 4) {
                    taskUser(newText);
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private FindUserAdapter.onClickListener onClickListener() {
        return new FindUserAdapter.onClickListener() {
            @Override
            public void onClickListener(View view, int idx) {
                User u = findUsers.get(idx);
                sendPush(u);
            }

        };
    }

    private FindUserAdapter.ListenerClickViewHolder onItemViewHolderClicked() {
        return new FindUserAdapter.ListenerClickViewHolder() {
            @Override
            public void onClickListenerViewHolder(User user) {
//                MainTab mainTab = setUserIntoMainTab(user);
//                mainTab.getmViewPager().setCurrentItem(2);
            }
        };
    }

    @NonNull
    private MainTab setUserIntoMainTab(User user) {
        MainTab mainTab = (MainTab) getActivity();
//        TabAdapter adapter = (TabAdapter) mainTab.getmViewPager().getAdapter();
//        adapter.setUser(user);
        return mainTab;
    }

    private void taskUser(String user) {
        UserService.getUser(getContext(), getListenerUser(), user);
    }

    private void sendPush(final User user){

        try {
            OneSignal.postNotification(new JSONObject("{'headings': {'en':'MaiMudi - Solicitação'}," +
                            "'android_group': {'en': 'MaiMudi'}," +
                            "'android_group_message': {'en': 'Você tem $[notif_count] novas notificações'}," +
                            "'big_picture': '"+ MaiMudiApplication.getInstance().getLoggedUser().getProfile_img()+"'," +
                            "'contents': {'en':'" + MaiMudiApplication.getInstance().getLoggedUser().getNome() + " solicitou uma amizade'}, " +
                            "'include_player_ids': ['" + user.getOne_signal_id() + "']}"),
                    new OneSignal.PostNotificationResponseHandler() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                            saveUserFriend(user);
                        }

                        @Override
                        public void onFailure(JSONObject response) {
                            Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveUserFriend(User user){

        User u = new User(user.getNome(),user.getNickname(),user.getOne_signal_id(),"esperando");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.ref_firebase_friends));
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child(user.getUid()).setValue(u);

        User uAuth = new User(MaiMudiApplication.getInstance().getLoggedUser().getNome(),MaiMudiApplication.getInstance().getLoggedUser().getNickname(),MaiMudiApplication.getInstance().getLoggedUser().getOne_signal_id(),"escolher");
        ref.child(user.getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).setValue(uAuth);

    }

    private UserService.OnReadUser getListenerUser() {
        return new UserService.OnReadUser() {
            @Override
            public void onFinishedReadUsers(List<User> users) {

                if(findUsers == null)
                    findUsers = new ArrayList<>();
                findUsers.clear();
                findUsers = users;

                if (users != null && users.size() > 0) {
                    llEmptyFriends.setVisibility(View.GONE);
                    findUserAdapter = new FindUserAdapter(getContext(), users, onClickListener(), onItemViewHolderClicked());
                    recyclerView.setAdapter(findUserAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    llEmptyFriends.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        };
    }
}

