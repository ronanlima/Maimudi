package br.com.maimudi.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.activity.SelectMusicActivity;
import br.com.maimudi.adapter.TimelineAdapter;
import br.com.maimudi.dialog.DialogComments;
import br.com.maimudi.dialog.DialogLikes;
import br.com.maimudi.model.Comment;
import br.com.maimudi.model.Timeline;
import br.com.maimudi.services.TimelineService;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class TimelineFragment extends Fragment { //implements UtilConnectivityService.IDisableConnection{
    private static final String TAG = TimelineFragment.class.getCanonicalName().toUpperCase();

    protected RecyclerView recyclerView;
    private List<Timeline> timeline;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout llProgressBar, llEmptyTimeline;
    private FloatingActionButton floatingActionButton;
    private TimelineAdapter timelineAdapter;
    private CoordinatorLayout coordinatorLayout;
    private MediaPlayer mediaPlayer;

    public static TimelineFragment newInstance(MediaPlayer mediaPlayer){
        TimelineFragment tf = new TimelineFragment();
        tf.setMediaPlayer(mediaPlayer);
        return tf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        llProgressBar = (LinearLayout) view.findViewById(R.id.ll_progress_timeline);
//        llNoConnection = (LinearLayout) view.findViewById(R.id.no_connection);
        llEmptyTimeline = (LinearLayout) view.findViewById(R.id.ll_empty_timeline);
        swipeRefreshLayout.setOnRefreshListener(OnRefreshListener());

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (getMediaPlayer() != null){
                    getMediaPlayer().stop();
                    getMediaPlayer().reset();
                }
                if (dy > 0){
                    floatingActionButton.hide();
                } else if (dy < 0){
                    floatingActionButton.show();
                }
            }
        });

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_post);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TimelineFragment.this.getActivity(), SelectMusicActivity.class));
                onDestroyView();
            }
        });

        return  view;
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener(){
        return new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(300);
                    taskTimeline();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private TimelineAdapter.onClickListener onClickListener() {
        return new TimelineAdapter.onClickListener() {
            @Override
            public void onClickListener(View view, int idx, String where) {
                final Timeline t = timeline.get(idx);
                if(where.equalsIgnoreCase("SING")){
                    atualizaLikesPost(view, t);
                    timelineAdapter.notifyDataSetChanged();
                }else if(where.equalsIgnoreCase("COMMENT")){
                    if (t.isComment()){
                        ((TextView) view.findViewById(R.id.commentEditText)).setText("");
                        floatingActionButton.show();
                    } else {
                        floatingActionButton.hide();
                    }
                    t.setImgComment(t.isComment() ? R.drawable.comment : R.drawable.comment_blue);
                    t.setComment(!t.isComment());
                    timelineAdapter.notifyDataSetChanged();
                }else if(where.equalsIgnoreCase("SEND")){
                    gravaCommentFirebase(view, t);
                }else if(where.equalsIgnoreCase("DIALOG_COMMENT")){
                    if (t.getComments() != null && t.getComments().size() > 0){
                        FragmentManager fm = getFragmentManager();
                        Bundle b = new Bundle();
                        b.putSerializable("TIMELINE",t);
                        DialogComments dialogFragment = new DialogComments();
                        dialogFragment.setArguments(b);
                        dialogFragment.setTargetFragment(TimelineFragment.this,100);
                        dialogFragment.show(fm, "Comments");
                    }
                }else if(where.equalsIgnoreCase("DIALOG_LIKES")){
                    if (t.getLikes() != null && t.getLikes() > 0){
                        FragmentManager fm = getFragmentManager();
                        Bundle b = new Bundle();
                        b.putSerializable("TIMELINE",t);
                        DialogLikes dialogFragment = new DialogLikes();
                        dialogFragment.setArguments(b);
                        dialogFragment.setTargetFragment(TimelineFragment.this,100);
                        dialogFragment.show(fm, "Likes");
                    }
                }else if(where.equalsIgnoreCase("PLAY_MUSIC")){
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
                }else if(where.equalsIgnoreCase("STOP_MUSIC")){
                    Log.v(TAG, "ACTION_UP end record for song ="+t.getPreview());
                    getMediaPlayer().stop();
                    getMediaPlayer().reset();
                }
            }
        };
    }

    private void atualizaLikesPost(View view, Timeline t) {
        int likes;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.ref_firebase_likes));
        ((TextView)view.findViewById(R.id.likes)).setText(getResources().getString(R.string.like, t.getLikes() != null && t.getLikes() != 0 ? t.getLikes() : "Ninguém"));
        if (t.isCantandoJunto()){
            ((ImageView)view.findViewById(R.id.sing)).setImageResource(R.drawable.microphone_black_icon);
            likes = t.getLikes() != null ? t.getLikes() : 0;
            likes--;
            ref.child(t.getId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
            t.setCantandoJunto(false);
        } else {
            ((ImageView)view.findViewById(R.id.sing)).setImageResource(R.drawable.microphone_blue_icon);
            likes = t.getLikes() != null ? t.getLikes() : 0;
            likes++;
            ref.child(t.getId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("nickname").setValue(MaiMudiApplication.getInstance().getLoggedUser().getNickname());
            ref.child(t.getId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile_img").setValue(MaiMudiApplication.getInstance().getLoggedUser().getProfile_img());
            t.setCantandoJunto(true);
        }
        t.setLikes(likes);
    }

    private void gravaCommentFirebase(final View view, final Timeline t) {
        String comentario = ((EditText) view.findViewById(R.id.commentEditText)).getText().toString();
        if (comentario != null && !comentario.trim().isEmpty()){
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            final Comment c = instanciaComment(comentario);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.ref_firebase_comments));
            ref.child(t.getId()).push().setValue(c).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.msg_comentario_sucesso), Snackbar.LENGTH_LONG);
                    ViewGroup group = (ViewGroup) snackbar.getView();
                    group.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    snackbar.show();
                    ((TextView) view.findViewById(R.id.commentEditText)).setText("");
                    timelineAdapter.notifyDataSetChanged();
                }
            });
        }
        t.setComment(false);
        t.setImgComment(R.drawable.comment);
    }

    @NonNull
    private Comment instanciaComment(String comentario) {
        final Comment c = new Comment();
        c.setComment(comentario);
        c.setNickname(MaiMudiApplication.getInstance().getLoggedUser().getNickname());
        c.setTimestamp(new Date().getTime());
        c.setUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        c.setUser_img(MaiMudiApplication.getInstance().getLoggedUser().getProfile_img());
        return c;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        if (timeline == null) {
            taskTimeline();
        }
    }

    private void taskTimeline() {
        if (!UtilConnectivityService.isConnected(getActivity())){
            Bundle bundle = new Bundle();
            bundle.putSerializable("LISTENER_CANCELLED", TimelineFragment.this);
            UtilConnectivityService.createNetErrorDialog(getActivity(), bundle, "Você está sem acesso à internet. Por favor habilite sua conexão.");
        } else {
            TimelineService.getTimeline(getContext(), new ArrayList<Timeline>(), getListenerTimeline());
        }
    }

    @Override
    public void onCancelledNetwork(Context context) {
        llProgressBar.setVisibility(View.GONE);
        llNoConnection.setVisibility(View.VISIBLE);
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskTimeline();
    }

    private void taskTimeline() {
        TimelineService.getTimeline(getContext(), new ArrayList<Timeline>(), getListenerTimeline());
    }

    private TimelineService.OnReadListenerTimeline getListenerTimeline(){
        return new TimelineService.OnReadListenerTimeline() {
            @Override
            public void onFinishReadTimeline(List<Timeline> listaTimeline) {
                llProgressBar.setVisibility(View.GONE);
                if (listaTimeline != null && listaTimeline.size() > 0){
                    llEmptyTimeline.setVisibility(View.GONE);
                    timeline = listaTimeline;
                    Collections.sort(timeline);
                    timelineAdapter = new TimelineAdapter(getContext(), onClickListener());
                    timelineAdapter.setTimeline(timeline);
                    recyclerView.setAdapter(timelineAdapter);
                } else {
                    llEmptyTimeline.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }

    public void setMediaPlayer(MediaPlayer mediaPlayer) { this.mediaPlayer = mediaPlayer; }

}


