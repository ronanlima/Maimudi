package br.com.maimudi.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.adapter.LikesAdapter;
import br.com.maimudi.model.Friend;
import br.com.maimudi.model.Timeline;
import br.com.maimudi.services.LikesService;

/**
 * Created by brunolemgruber on 20/03/16.
 */
public class DialogLikes extends DialogFragment {

    protected RecyclerView recyclerView;
    private List<Friend> friends;
    private LinearLayoutManager mLayoutManager;
    private Timeline timeline;

    public DialogLikes() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        timeline = (Timeline) getArguments().getSerializable("TIMELINE");

        View view = inflater.inflate(R.layout.dialog_likes, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskLikes();
    }

    private void taskLikes() {
        LikesService.getLikes(getContext(), getTimeline(), getListenerLikes());
        recyclerView.setAdapter(new LikesAdapter(getContext(), friends));
    }

    private LikesService.OnReadListenerLikes getListenerLikes(){
        return new LikesService.OnReadListenerLikes() {
            @Override
            public void onFinishReadLikes(List<Friend> friends) {
                recyclerView.setAdapter(new LikesAdapter(getContext(), friends));
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }
}
