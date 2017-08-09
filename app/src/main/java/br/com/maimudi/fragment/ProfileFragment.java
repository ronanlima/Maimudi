package br.com.maimudi.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.activity.EditProfileActivity;
import br.com.maimudi.activity.MainTab;
import br.com.maimudi.adapter.ProfileAdapter;
import br.com.maimudi.adapter.TabAdapter;
import br.com.maimudi.model.Friend;
import br.com.maimudi.model.Profile;
import br.com.maimudi.model.Timeline;
import br.com.maimudi.services.ProfileService;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.getCanonicalName().toUpperCase();

    protected RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout llProgressBar, llNoPosts;
    private Profile mprofile;
    private TextView user,nickname,mudis,listening,listener, editProfile;
    private CircleImageView circleImageView;
    private ImageView background, charts,icCharts,icFindFriends,icEditUser;
    private ProgressBar loading;
    private Friend mFriend;
    private ProfileService profileService;
    private MediaPlayer mediaPlayer;
    private OnDrawChartByUserData listenerCharts;

    public static ProfileFragment newInstance(MediaPlayer mediaPlayer){
        ProfileFragment pf = new ProfileFragment();
        pf.setMediaPlayer(mediaPlayer);
        return pf;
    }

    public interface OnDrawChartByUserData extends Serializable{
        void drawCharts(Friend f);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        final MainTab mainTab = (MainTab) getActivity();
        TabAdapter adapter = (TabAdapter) mainTab.getmViewPager().getAdapter();
        mFriend = adapter.getFriend();
        listenerCharts = adapter.getListenerCharts();

        recuperaImagemBackgroundUsuario(mFriend.getUser());
        icCharts = (ImageView) view.findViewById(R.id.ico_chart);
        icEditUser = (ImageView) view.findViewById(R.id.ic_edit_user);
        icFindFriends = (ImageView) view.findViewById(R.id.ic_find_friends);
        llProgressBar = (LinearLayout) view.findViewById(R.id.ll_progress_profile);
        llNoPosts = (LinearLayout) view.findViewById(R.id.ll_no_posts);
        //loading = (ProgressBar) view.findViewById(R.id.progress_background_image);
        circleImageView = (CircleImageView) view.findViewById(R.id.profile_image);
        background = (ImageView) view.findViewById(R.id.profile_background);
        charts = (ImageView) view.findViewById(R.id.ico_charts);
        charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainTab.getmViewPager().setCurrentItem(2);
                listenerCharts.drawCharts(mFriend);
            }
        });
        user = (TextView) view.findViewById(R.id.user);
        nickname = (TextView) view.findViewById(R.id.nickname);
        mudis = (TextView) view.findViewById(R.id.mudis);
        listener = (TextView) view.findViewById(R.id.listener);
        listening = (TextView) view.findViewById(R.id.listening);
        if (mFriend != null && mFriend.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            icEditUser.setVisibility(View.VISIBLE);
            icEditUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
            });
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (getMediaPlayer() != null){
                    getMediaPlayer().stop();
                    getMediaPlayer().reset();
                }
            }
        });
        return  view;
    }

    private void recuperaImagemBackgroundUsuario(final String idUser) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.storage_firebase_url)).child(getResources().getString(R.string.ref_firebase_users)).child(idUser).child(getResources().getString(R.string.ref_storage_profile_background));
        final long THRE_MEGABYTE = 1024 * 3072;
        ref.getBytes(THRE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //Como o fragment pode ser removido, esse tratamento eh necessario
                if (getContext() != null){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    ImageView imgView = new ImageView(getContext());
                    imgView.setImageBitmap(bitmap);
                    if (idUser.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        MaiMudiApplication.getInstance().getLoggedUser().setImageBackground(imgView);
                    }
                    background.setImageDrawable(imgView.getDrawable());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void inicializaInfoUsuario(){
        if (mFriend == null ) {
            if (mFriend.getUser() != null && !mFriend.getUser().isEmpty()) {
                setNameNicknameIntoTextViews(mFriend.getNome(), mFriend.getNickname());
            }
            if (mFriend.getImgUrl() != null && !mFriend.getImgUrl().isEmpty()) {
                Glide.with(this).load(mFriend.getImgUrl()).into(circleImageView);
            } else if (mFriend.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                    MaiMudiApplication.getInstance().getLoggedUser().getImageProfile() != null){
                circleImageView.setImageDrawable(MaiMudiApplication.getInstance().getLoggedUser().getImageProfile().getDrawable());
            }else if (mFriend.getImgUrl() == null || mFriend.getImgUrl().isEmpty()){
                circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.user_mudi));
            }
        } else {
            if (MaiMudiApplication.getInstance().getLoggedUser().getImageProfile() != null){
                circleImageView.setImageDrawable(MaiMudiApplication.getInstance().getLoggedUser().getImageProfile().getDrawable());
            } else {
                circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.user_mudi));
            }
            if (MaiMudiApplication.getInstance().getLoggedUser().getImageBackground() != null){
                background.setImageDrawable(MaiMudiApplication.getInstance().getLoggedUser().getImageBackground().getDrawable());
            }
            setNameNicknameIntoTextViews(MaiMudiApplication.getInstance().getLoggedUser().getNome(), MaiMudiApplication.getInstance().getLoggedUser().getNickname());
        }
    }

    private void setNameNicknameIntoTextViews(String nome, String nicknameUser) {
        user.setText(nome);
        nickname.setText(nicknameUser);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        inicializaInfoUsuario();
        taskProfile();
    }

    private void taskProfile() {
        String id = mFriend != null ? mFriend.getUser() : FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileService = new ProfileService();
        profileService.getProfile(getContext(), id, mFriend != null ? mFriend : null, onProfileLoaded());
    }

    private ProfileService.OnProfileLoaded onProfileLoaded(){
        return new ProfileService.OnProfileLoaded() {
            @Override
            public void onFinishReadProfile(Profile profile) {
                Collections.sort(profile.getTimeline(), new Comparator<Timeline>() {
                    @Override
                    public int compare(Timeline timeline, Timeline t1) {
                        return t1.getTimestamp().compareTo(timeline.getTimestamp());
                    }
                });
                llProgressBar.setVisibility(View.GONE);
                mudis.setText(getResources().getString(R.string.mudis, String.format("%02d", Integer.parseInt(profile.getMudis()))));
                listener.setText(getResources().getString(R.string.listener, String.format("%02d", Integer.parseInt(profile.getListener()))));
                listening.setText(getResources().getString(R.string.listening, String.format("%02d", Integer.parseInt(profile.getListening()))));
                if (profile.getTimeline().size() > 0){
                    llNoPosts.setVisibility(View.GONE);
                    mprofile = profile;
                    recyclerView.setAdapter(new ProfileAdapter(getContext(), profile.getTimeline(), onPlayMusic()));
                } else {
                    recyclerView.setVisibility(View.GONE);
                    llNoPosts.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private ProfileAdapter.onClickListener onPlayMusic(){
        return new ProfileAdapter.onClickListener() {
            @Override
            public void onClickListener(View view, int idx, String string) {
                Timeline t = mprofile.getTimeline().get(idx);
                if(string.equalsIgnoreCase("PLAY_MUSIC")){
                    if (getMediaPlayer() == null){
                        setMediaPlayer(new MediaPlayer());
                    }
                    getMediaPlayer().stop();
                    getMediaPlayer().reset();
                    getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        getMediaPlayer().setDataSource(t.getPreview());
                        getMediaPlayer().prepare();
                        getMediaPlayer().start();
                        Log.v(TAG, "ACTION_DOWN start record for song ="+t.getPreview());
                    } catch (IOException e) {
                        Log.v(TAG, "ACTION_DOWN some problem happen");
                        e.printStackTrace();
                    }
                }else if(string.equalsIgnoreCase("STOP_MUSIC")){
                    Log.v(TAG, "ACTION_UP end record for song ="+t.getPreview());
                    getMediaPlayer().stop();
                    getMediaPlayer().reset();
                }
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (profileService.getRefUsers() != null){
            profileService.getRefUsers().removeEventListener(profileService.getListenerUsers());
        }
        if (profileService.getRefLikes() != null){
            profileService.getRefLikes().removeEventListener(profileService.getListenerLikes());
        }
        if (profileService.getRefComment() != null){
            profileService.getRefComment().removeEventListener(profileService.getListenerComments());
        }
        if (profileService.getRefPosts() != null){
            profileService.getRefPosts().removeEventListener(profileService.getListenerPosts());
        }
    }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }

    public void setMediaPlayer(MediaPlayer mediaPlayer) { this.mediaPlayer = mediaPlayer; }
}


