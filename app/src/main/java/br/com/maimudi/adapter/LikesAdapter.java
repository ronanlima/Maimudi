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
import br.com.maimudi.model.Friend;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.FriendsViewHolder> {

    protected static final String TAG = LikesAdapter.class.getCanonicalName().toUpperCase();
    private final List<Friend> friends;
    private final Context context;

    public LikesAdapter(Context context, List<Friend> friends) {
        this.context = context;
        this.friends = friends;
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_like, parent, false);
        FriendsViewHolder holder = new FriendsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {
        Friend f = friends.get(position);

        Glide.with(context).load(f.getImgUrl()).into(holder.img);
        holder.nickname.setText(f.getNickname());
    }

    @Override
    public int getItemCount() {
        return this.friends != null ? this.friends.size() : 0;
    }

    // ViewHolder com as views
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname,comment;
        ImageView img;

        public FriendsViewHolder(View view) {
            super(view);

            nickname = (TextView) view.findViewById(R.id.nickname);
            img = (ImageView) view.findViewById(R.id.profile_image);
        }
    }
}
