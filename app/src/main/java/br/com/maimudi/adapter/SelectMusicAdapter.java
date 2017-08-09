package br.com.maimudi.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.maimudi.R;
import br.com.maimudi.model.Music;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class SelectMusicAdapter extends RecyclerView.Adapter<SelectMusicAdapter.MusicViewHolder> {

    protected static final String TAG = SelectMusicAdapter.class.getCanonicalName().toUpperCase();
    private List<Music> musics;
    private final Context context;
    private onClickListener onClickListener;
    private onPlayMusic onPlayMusic;
    private ArrayList<Music> arraylist;

    public SelectMusicAdapter(Context context, List<Music> musics,onClickListener onClickListener, onPlayMusic onPlayMusic) {
        this.context = context;
        setMusics(musics);
        this.onClickListener = onClickListener;
        this.onPlayMusic = onPlayMusic;
        setArraylist(new ArrayList<Music>());
        getArraylist().addAll(musics);
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_select_music, parent, false);
        MusicViewHolder holder = new MusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MusicViewHolder holder, final int position) {
        Music m = getMusics().get(position);

        Glide.with(context).load(m.getImgSpotify()).into(holder.img);
        holder.artista.setText(m.getArtista());
        holder.music.setText(m.getMusica());
        holder.frameLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        holder.frameLayout.setOnTouchListener(listenerToPlayMusic(holder.itemView, position));

        if (onClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickListener(holder.itemView, position);
                }
            });
        }
    }

    public View.OnTouchListener listenerToPlayMusic(final View view, final int position){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        onPlayMusic.onPlayMusic(view, position, "STOP_MUSIC");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        onPlayMusic.onPlayMusic(view, position, "PLAY_MUSIC");
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
    }

    public interface onClickListener  {
        void onClickListener(View view, int idx);
    }

    public interface onPlayMusic extends Serializable{
        void onPlayMusic(View view, int idx, String string);
    }

    @Override
    public int getItemCount() {
        return this.musics != null ? this.musics.size() : 0;
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout frameLayout;
        private ImageView img;
        private TextView artista, music;
        private View view;

        public MusicViewHolder(View view) {
            super(view);
            this.view = view;

            frameLayout = (FrameLayout) view.findViewById(R.id.fl_select_music);
            img = (ImageView) view.findViewById(R.id.img);
            artista = (TextView) view.findViewById(R.id.artista);
            music = (TextView) view.findViewById(R.id.music);
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        getMusics().clear();
        if (charText.length() == 0) {
            getMusics().addAll(getArraylist());
        } else {
            for (Music m : getArraylist()) {
                if (m.getArtista().toLowerCase(Locale.getDefault()).contains(charText) || m.getMusica().toLowerCase(Locale.getDefault()).contains(charText)) {
                    getMusics().add(m);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setCopyMusicsIntoArrayList(List<Music> musics){
        setMusics(musics);
        getArraylist().addAll(getMusics());
    }

    public ArrayList<Music> getArraylist() { return arraylist; }

    public void setArraylist(ArrayList<Music> arraylist) { this.arraylist = arraylist; }

    public List<Music> getMusics() { return musics; }

    public void setMusics(List<Music> musics) { this.musics = musics; }
}
