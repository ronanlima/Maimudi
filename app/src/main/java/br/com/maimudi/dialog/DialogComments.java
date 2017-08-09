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
import br.com.maimudi.adapter.CommentsAdapter;
import br.com.maimudi.model.Comment;
import br.com.maimudi.model.Timeline;
import br.com.maimudi.services.CommentsService;

/**
 * Created by brunolemgruber on 20/03/16.
 */
public class DialogComments extends DialogFragment {

    protected RecyclerView recyclerView;
    private List<Comment> comments;
    private LinearLayoutManager mLayoutManager;
    private Timeline timeline;

    public DialogComments() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        timeline = (Timeline) getArguments().getSerializable("TIMELINE");

        View view = inflater.inflate(R.layout.dialog_comment, container, false);
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
        taskFriends();
    }

    private void taskFriends() {
        CommentsService.getComment(getContext(), getTimeline(), getListenerComments());
        recyclerView.setAdapter(new CommentsAdapter(getContext(), comments));
    }

    private CommentsService.OnReadListenerComments getListenerComments(){
        return new CommentsService.OnReadListenerComments() {
            @Override
            public void onFinishReadComments(List<Comment> comments) {
                recyclerView.setAdapter(new CommentsAdapter(getContext(), comments));
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
