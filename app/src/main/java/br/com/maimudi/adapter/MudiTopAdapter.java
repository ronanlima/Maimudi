package br.com.maimudi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import br.com.maimudi.R;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class MudiTopAdapter extends RecyclerView.Adapter<MudiTopAdapter.AlbumViewHolder> {

    protected static final String TAG = MudiTopAdapter.class.getCanonicalName().toUpperCase();
    private final List<Integer> albuns;
    private final Context context;
    private onClickListener onClickListener;

    public MudiTopAdapter(Context context, List<Integer> albuns, onClickListener onClickListener) {
        this.context = context;
        this.albuns = albuns;
        this.onClickListener = onClickListener;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_top_item, parent, false);
        AlbumViewHolder holder = new AlbumViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder holder, final int position) {

        //Timeline t = timeline.get(position);

        holder.img.setImageResource(albuns.get(position));

        if (onClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickListener(holder.itemView, position);
                }
            });

        }

    }

    public interface onClickListener  {
        void onClickListener(View view, int idx);
    }

    @Override
    public int getItemCount() {
        return this.albuns != null ? this.albuns.size() : 0;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public AlbumViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.img);

        }
    }
}
