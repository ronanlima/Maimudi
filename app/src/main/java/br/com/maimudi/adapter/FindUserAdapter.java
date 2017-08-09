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
import br.com.maimudi.model.User;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class FindUserAdapter extends RecyclerView.Adapter<FindUserAdapter.FindUserViewHolder> {

    protected static final String TAG = FindUserAdapter.class.getCanonicalName().toUpperCase();
    private final List<User> users;
    private final Context context;
    private onClickListener onClickListener;
    private ListenerClickViewHolder listenerViewHolder;

    public FindUserAdapter(Context context, List<User> users, onClickListener onClickListener, ListenerClickViewHolder listenerViewHolder) {
        this.context = context;
        this.users = users;
        this.onClickListener = onClickListener;
        this.listenerViewHolder = listenerViewHolder;
    }

    @Override
    public FindUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_find_user, parent, false);
        FindUserViewHolder holder = new FindUserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FindUserViewHolder holder, final int position) {
        final User u = users.get(position);

        if (u.getProfile_img() != null && !u.getProfile_img().isEmpty()){

            Glide.with(context).load(u.getProfile_img()).listener(new RequestListener<String, GlideDrawable>() {
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

        holder.user.setText(u.getNome());
        holder.nickname.setText(u.getNickname());
        //Todos usuários se seguem
        //holder.img.setImageResource(R.drawable.headset_green);
        //holder.txtFollow.setText(context.getResources().getString(R.string.label_ouvindo));
        holder.llViewItemGeral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerViewHolder.onClickListenerViewHolder(u);
            }
        });

        holder.llHeadSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickListener(holder.itemView, position);
            }
        });
    }

    public interface onClickListener  {
        void onClickListener(View view, int idx);
    }

    public interface ListenerClickViewHolder extends Serializable{
        void onClickListenerViewHolder(User User);
    }

    @Override
    public int getItemCount() {
        return this.users != null ? this.users.size() : 0;
    }

    public static class FindUserViewHolder extends RecyclerView.ViewHolder {
        public TextView user,nickname,txtFollow;
        ImageView img,profile_image;
        LinearLayout llHeadSet, llViewItemGeral;
        public ProgressBar progressBar;

        public FindUserViewHolder(View view) {
            super(view);

            user = (TextView) view.findViewById(R.id.user);
            nickname = (TextView) view.findViewById(R.id.nickname);
            txtFollow = (TextView) view.findViewById(R.id.txt_follow);
            img = (ImageView) view.findViewById(R.id.follow);
            profile_image = (ImageView) view.findViewById(R.id.profile_image);
            llHeadSet = (LinearLayout) view.findViewById(R.id.ll_headset);
            llViewItemGeral = (LinearLayout) view.findViewById(R.id.view_holder_friend);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
        }
    }
}
