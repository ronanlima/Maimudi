package br.com.maimudi.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.activity.MainTab;
import br.com.maimudi.adapter.FriendsAdapter;
import br.com.maimudi.adapter.TabAdapter;
import br.com.maimudi.model.Friend;
import br.com.maimudi.model.User;
import br.com.maimudi.services.FriendsService;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class FriendsFragment extends Fragment {

    protected RecyclerView recyclerView,recyclerView2,recyclerView3;
    private LinearLayout llEmptyFriends;
    private List<Friend> acceptedFriends,waitFriends,chooseFriends;
    private LinearLayoutManager mLayoutManager,mLayoutManager2,mLayoutManager3;
    private FriendsAdapter friendsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends, container, false);

        acceptedFriends = new ArrayList<>();
        waitFriends = new ArrayList<>();
        chooseFriends = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView2 = (RecyclerView) view.findViewById(R.id.recyclerView2);
        recyclerView3 = (RecyclerView) view.findViewById(R.id.recyclerView3);
        llEmptyFriends = (LinearLayout) view.findViewById(R.id.ll_empty_friends);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager2 = new LinearLayoutManager(getActivity());
        mLayoutManager3 = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView2.setLayoutManager(mLayoutManager2);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setHasFixedSize(true);
        recyclerView3.setLayoutManager(mLayoutManager3);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setHasFixedSize(true);

        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskFriends("aceito");
    }

    private FriendsAdapter.onClickListener onClickListener() {
        return new FriendsAdapter.onClickListener() {
            @Override
            public void onClickListener(View view, int idx) {
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onClickListenerAccepted(View view, int idx) {
                Friend f = chooseFriends.get(idx);
                acceptedFriend(f);
            }

            @Override
            public void onClickListenerDenied(View view, int idx) {
                Friend f = chooseFriends.get(idx);
                deniedFriend(f);
            }

        };
    }

    private void acceptedFriend(Friend friend){

        User u = new User(friend.getNome(),friend.getNickname(),friend.getOne_signal_id(),"aceito");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.ref_firebase_friends));
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child(friend.getUser()).setValue(u);

        User uAuth = new User(MaiMudiApplication.getInstance().getLoggedUser().getNome(),MaiMudiApplication.getInstance().getLoggedUser().getNickname(),MaiMudiApplication.getInstance().getLoggedUser().getOne_signal_id(),"aceito");
        ref.child(friend.getUser()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).setValue(uAuth);

        taskFriends("aceito");
        chooseFriends.clear();
        waitFriends.clear();
        acceptedFriends.clear();

    }

    private void deniedFriend(Friend friend){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.ref_firebase_friends));
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child(friend.getUser()).removeValue();

        MaiMudiApplication.getInstance().getLoggedUser().setStatus("escolher");
        ref.child(friend.getUser()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).removeValue();

        taskFriends("aceito");
        chooseFriends.clear();
        waitFriends.clear();
        acceptedFriends.clear();

    }

    private FriendsAdapter.ListenerClickViewHolder onItemViewHolderClicked(){
        return new FriendsAdapter.ListenerClickViewHolder() {
            @Override
            public void onClickListenerViewHolder(Friend friend) {
                MainTab mainTab = setFriendIntoMainTab(friend);
                mainTab.getmViewPager().setCurrentItem(3);
            }
        };
    }

    @NonNull
    private MainTab setFriendIntoMainTab(Friend friend) {
        MainTab mainTab = (MainTab) getActivity();
        TabAdapter adapter = (TabAdapter) mainTab.getmViewPager().getAdapter();
        adapter.setFriend(friend);
        return mainTab;
    }

    private void taskFriends(String status) {
        if(status.equalsIgnoreCase("aceito")){
            FriendsService.getFriends(getContext(), getListenerFriends(),status);
        }else if(status.equalsIgnoreCase("esperando")){
            FriendsService.getWaitFriends(getContext(), getListenerFriends(),status);
        }else{
            FriendsService.getChooseFriends(getContext(), getListenerFriends(),status);
        }
    }

    private FriendsService.OnReadFriends getListenerFriends(){
        return new FriendsService.OnReadFriends() {
            @Override
            public void onFinishedReadFriends(List<Friend> friends) {
                Friend f = new Friend();
                f.setUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                f.setQtdAmigos(friends.size());
                setFriendIntoMainTab(f);
                if (friends != null && friends.size() > 0){
                    acceptedFriends = friends;
                    llEmptyFriends.setVisibility(View.GONE);
                    friendsAdapter = new FriendsAdapter(getContext(), friends, onClickListener(), onItemViewHolderClicked());
                    recyclerView.setAdapter(friendsAdapter);
                    recyclerView.setVisibility(View.VISIBLE);

                }

                taskFriends("esperando");
            }

            @Override
            public void onFinishedReadWaitFriends(List<Friend> friends) {
                if (friends != null && friends.size() > 0){
                    waitFriends = friends;
                    llEmptyFriends.setVisibility(View.GONE);
                    friendsAdapter = new FriendsAdapter(getContext(), friends, onClickListener(), onItemViewHolderClicked());
                    recyclerView2.setAdapter(friendsAdapter);
                    recyclerView2.setVisibility(View.VISIBLE);
                }

                taskFriends("escolher");
            }

            @Override
            public void onFinishedReadChooseFriends(List<Friend> friends) {
                if (friends != null && friends.size() > 0){
                    chooseFriends = friends;
                    llEmptyFriends.setVisibility(View.GONE);
                    friendsAdapter = new FriendsAdapter(getContext(), friends, onClickListener(), onItemViewHolderClicked());
                    recyclerView3.setAdapter(friendsAdapter);
                    recyclerView3.setVisibility(View.VISIBLE);

                }

                if(waitFriends.size() == 0 && chooseFriends.size() == 0 && acceptedFriends.size() == 0){
                    llEmptyFriends.setVisibility(View.VISIBLE);
                }
            }
        };
    }

}
