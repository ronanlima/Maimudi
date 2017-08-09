package br.com.maimudi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.model.Comment;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.FriendsViewHolder> {

    protected static final String TAG = CommentsAdapter.class.getCanonicalName().toUpperCase();
    private final List<Comment> comments;
    private final Context context;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_comment, parent, false);
        FriendsViewHolder holder = new FriendsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {
        Comment c = comments.get(position);

        Glide.with(context).load(c.getUser_img()).into(holder.img);
        holder.comment.setText(c.getComment());
        holder.nickname.setText(c.getNickname());
    }

    @Override
    public int getItemCount() {
        return this.comments != null ? this.comments.size() : 0;
    }

    // ViewHolder com as views
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname,comment;
        ImageView img;

        public FriendsViewHolder(View view) {
            super(view);

            comment = (TextView) view.findViewById(R.id.comment);
            nickname = (TextView) view.findViewById(R.id.nickname);
            img = (ImageView) view.findViewById(R.id.profile_image);
        }
    }
}
