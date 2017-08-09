package br.com.maimudi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.Serializable;
import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.model.Friend;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    protected static final String TAG = FriendsAdapter.class.getCanonicalName().toUpperCase();
    private final List<Friend> friends;
    private final Context context;
    private onClickListener onClickListener;
    private ListenerClickViewHolder listenerViewHolder;
    private static final int TYPE_WAITING = 0;
    private static final int TYPE_ACCEPTED = 1;
    private static final int TYPE_CHOOSE = 2;

    public FriendsAdapter(Context context, List<Friend> friends,onClickListener onClickListener, ListenerClickViewHolder listenerViewHolder) {
        this.context = context;
        this.friends = friends;
        this.onClickListener = onClickListener;
        this.listenerViewHolder = listenerViewHolder;
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if(viewType == 0){
            view = LayoutInflater.from(context).inflate(R.layout.card_wait_friends, parent, false);
        }else if (viewType == 1){
            view = LayoutInflater.from(context).inflate(R.layout.card_friends, parent, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.card_choose_friends, parent, false);
        }
        FriendsViewHolder holder = new FriendsViewHolder(view);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if(friends.get(position).getStatus().equalsIgnoreCase("aceito")){
            return TYPE_ACCEPTED;
        }else if(friends.get(position).getStatus().equalsIgnoreCase("esperando")){
            return TYPE_WAITING;
        }else {
            return TYPE_CHOOSE;
        }
    }

    @Override
    public void onBindViewHolder(final FriendsViewHolder holder, final int position) {
        final Friend f = friends.get(position);

        int type  = getItemViewType(position);

        if (f.getImgUrl() != null && !f.getImgUrl().isEmpty()){

            Glide.with(context).load(f.getImgUrl()).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.profile_image);
        }else{
            Glide.clear(holder.profile_image);
            holder.progressBar.setVisibility(View.GONE);
            holder.profile_image.setImageResource(R.drawable.user_mudi);
        }

        holder.user.setText(f.getNome());
        holder.nickname.setText(f.getNickname());

        if(type == 1){

            holder.mudis.setText(context.getResources().getString(R.string.mudis, f.getMudis() != null ? f.getMudis() : 0));
            //Todos usuários se seguem
            holder.listening.setText(context.getResources().getString(R.string.listener, friends.size()));
            holder.img.setImageResource(R.drawable.headset_green);
            holder.txtFollow.setText(context.getResources().getString(R.string.label_ouvindo));
            holder.llViewItemGeral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    f.setQtdAmigos(friends.size());
                    listenerViewHolder.onClickListenerViewHolder(f);
                }
            });
        }

        if(type == 2){

            holder.ll_accept.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                    onClickListener.onClickListenerAccepted(holder.itemView, position);
                }
            });

            holder.ll_denied.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                    onClickListener.onClickListenerDenied(holder.itemView, position);
                }
            });
        }
    }

    public interface onClickListener  {
        void onClickListener(View view, int idx);
        void onClickListenerAccepted(View view, int idx);
        void onClickListenerDenied(View view, int idx);
    }

    public interface ListenerClickViewHolder extends Serializable{
        void onClickListenerViewHolder(Friend friend);
    }

    @Override
    public int getItemCount() {
        return this.friends != null ? this.friends.size() : 0;
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView user,nickname,mudis,listening,txtFollow;
        ImageView img,profile_image;
        LinearLayout llHeadSet, llViewItemGeral,ll_accept,ll_denied;
        public ProgressBar progressBar;

        public FriendsViewHolder(View view) {
            super(view);

            user = (TextView) view.findViewById(R.id.user);
            nickname = (TextView) view.findViewById(R.id.nickname);
            mudis = (TextView) view.findViewById(R.id.mudis);
            listening = (TextView) view.findViewById(R.id.listener);
            txtFollow = (TextView) view.findViewById(R.id.txt_follow);
            img = (ImageView) view.findViewById(R.id.follow);
            profile_image = (ImageView) view.findViewById(R.id.profile_image);
            llHeadSet = (LinearLayout) view.findViewById(R.id.ll_headset);
            llViewItemGeral = (LinearLayout) view.findViewById(R.id.view_holder_friend);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
            ll_accept = (LinearLayout) view.findViewById(R.id.ll_accept);
            ll_denied = (LinearLayout) view.findViewById(R.id.ll_not_accept);
        }
    }
}
