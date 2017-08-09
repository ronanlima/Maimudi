package br.com.maimudi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.model.Timeline;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    protected static final String TAG = ProfileAdapter.class.getCanonicalName().toUpperCase();
    private final List<Timeline> timeline;
    private final Context context;
    private onClickListener clickListener;

    public ProfileAdapter(Context context, List<Timeline> timeline, onClickListener onClickListener) {
        this.context = context;
        this.timeline = timeline;
        this.clickListener = onClickListener;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_profile, parent, false);
        ProfileViewHolder holder = new ProfileViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        Timeline t = timeline.get(position);

        if (t.getPost_img() != null){
            Glide.with(context).load(t.getPost_img()).into(holder.img);
        } else {
            holder.img.setImageResource(R.drawable.timeline_img_card);
            Log.w(TAG, "No post '"+t.getId()+"' não existe uma capa de álbum!");
        }
        if (t.getUser_img() != null && !t.getUser_img().isEmpty()){
            Glide.with(context).load(t.getUser_img()).into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.user_mudi);
        }
        holder.user.setText(t.getUser());
        holder.time.setText(t.getTimestamp_formatado()); 
        //holder.reaction.setText(t.getFeeling()+"\t"+t.getAction());
        holder.reaction.setText(context.getResources().getString(R.string.msg_action_feeling_user, t.getFeeling() != null ? t.getFeeling() : "", t.getAction() != null ? t.getAction() : ""));
        holder.lyrics.setText(t.getLyric());
        holder.comments.setText(t.getCommentsNumber() != null ? context.getResources().getString(R.string.comments_number, t.getCommentsNumber()) : context.getResources().getString(R.string.label_nenhum_comentario));
        holder.likes.setText(context.getResources().getString(R.string.like, t.getLikes() != null ? t.getLikes() : context.getResources().getString(R.string.label_ninguem)));
        holder.frameLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        holder.frameLayout.setOnTouchListener(listenerToPlayMusic(holder.itemView, position));

        holder.lyrics.setVerticalScrollBarEnabled(true);
        holder.lyrics.setMovementMethod(new ScrollingMovementMethod());
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isLarger;

                isLarger = ((TextView) v).getLineCount()
                        * ((TextView) v).getLineHeight() > v.getHeight();
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        && isLarger) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                } else {
                    v.getParent().requestDisallowInterceptTouchEvent(false);

                }
                return false;
            }
        };
        holder.lyrics.setOnTouchListener(touchListener);
    }

    public View.OnTouchListener listenerToPlayMusic(final View view, final int position){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        clickListener.onClickListener(view, position, "STOP_MUSIC");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        clickListener.onClickListener(view, position, "PLAY_MUSIC");
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
    }

    public interface onClickListener  {
        void onClickListener(View view, int idx, String where);
    }

    @Override
    public int getItemCount() {
        return this.timeline != null ? this.timeline.size() : 0;
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView time,user,reaction,lyrics,comments,likes;
        ImageView img,imgProfile;
        FrameLayout frameLayout;

        public ProfileViewHolder(View view) {
            super(view);

            time = (TextView) view.findViewById(R.id.timestamp);
            frameLayout = (FrameLayout) view.findViewById(R.id.fmLayout);
            user = (TextView) view.findViewById(R.id.user);
            reaction = (TextView) view.findViewById(R.id.feeling);
            lyrics = (TextView) view.findViewById(R.id.lyric);
            comments = (TextView) view.findViewById(R.id.comments_number);
            likes = (TextView) view.findViewById(R.id.likes);
            img = (ImageView) view.findViewById(R.id.img);
            imgProfile = (ImageView) view.findViewById(R.id.profile_image);
        }
    }
}
