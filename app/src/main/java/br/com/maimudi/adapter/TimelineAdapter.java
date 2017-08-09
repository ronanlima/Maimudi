package br.com.maimudi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    protected static final String TAG = TimelineAdapter.class.getCanonicalName().toUpperCase();
    private List<Timeline> timeline;
    private final Context context;
    private Timeline t = null;
    private onClickListener onClickListener;

    public TimelineAdapter(Context context, TimelineAdapter.onClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_timeline, parent, false);
        TimelineViewHolder holder = new TimelineViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TimelineViewHolder holder, final int position) {

        t = getTimeline().get(position);

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
        holder.time.setText(t.getTimestamp_formatado());
        holder.user.setText(t.getUser());
        holder.reaction.setText(context.getResources().getString(R.string.msg_action_feeling_user, t.getFeeling() != null ? t.getFeeling() : "", t.getAction() != null ? t.getAction() : ""));
        holder.lyrics.setText(t.getLyric());
        String labelComments;
        if (t.getComments() != null && t.getComments().size() > 0){
            labelComments = context.getResources().getString(R.string.comments_number, t.getComments().size());
        } else {
            labelComments = context.getResources().getString(R.string.label_nenhum_comentario);
        }
        holder.comments.setText(labelComments);
        holder.likes.setText(context.getResources().getString(R.string.like, t.getLikes() != null && t.getLikes() != 0 ? t.getLikes() : context.getResources().getString(R.string.label_ninguem)));
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClickListener(holder.itemView, position,"DIALOG_LIKES");
            }
        });
        if (t.isCantandoJunto()){
            holder.sing.setImageResource(R.drawable.microphone_blue_icon);
        } else {
            holder.sing.setImageResource(R.drawable.microphone_black_icon);
        }
        holder.commentEditText.setVisibility(t.isComment() ? View.VISIBLE : View.GONE);
        holder.send.setVisibility(t.isComment() ? View.VISIBLE : View.GONE);
        holder.comment.setImageResource(t.getImgComment());

        holder.sing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickListener(holder.itemView, position,"SING");
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickListener(holder.itemView, position,"COMMENT");
            }
        });

        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickListener(holder.itemView, position,"SEND");
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickListener(holder.itemView, position,"DIALOG_COMMENT");
            }
        });

        if (t.getPreview() != null){
            holder.frameLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
            holder.frameLayout.setOnTouchListener(listenerToPlayMusic(holder.itemView, position));
        }

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
                        onClickListener.onClickListener(view, position, "STOP_MUSIC");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        onClickListener.onClickListener(view, position, "PLAY_MUSIC");
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

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        TextView time,user,reaction,lyrics,comments,likes;
        ImageView img,sing,comment,share,send,imgProfile;
        EditText commentEditText;
        FrameLayout frameLayout;

        public TimelineViewHolder(View view) {
            super(view);

            time = (TextView) view.findViewById(R.id.timestamp);
            user = (TextView) view.findViewById(R.id.user);
            reaction = (TextView) view.findViewById(R.id.feeling);
            lyrics = (TextView) view.findViewById(R.id.lyric);
            comments = (TextView) view.findViewById(R.id.comments_number);
            likes = (TextView) view.findViewById(R.id.likes);
            img = (ImageView) view.findViewById(R.id.img);
            sing = (ImageView) view.findViewById(R.id.sing);
            comment = (ImageView) view.findViewById(R.id.comment);
            //share = (ImageView) view.findViewById(R.id.share);
            send = (ImageView) view.findViewById(R.id.send);
            commentEditText = (EditText) view.findViewById(R.id.commentEditText);
            frameLayout = (FrameLayout) view.findViewById(R.id.fmLayout);
            imgProfile = (ImageView) view.findViewById(R.id.profile_image);
        }
    }

    public List<Timeline> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<Timeline> timeline) {
        this.timeline = timeline;
    }
}
