package br.com.maimudi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.model.Mood;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class MoodTopAdapter extends RecyclerView.Adapter<MoodTopAdapter.MoodsViewHolder> {

    protected static final String TAG = MoodTopAdapter.class.getCanonicalName().toUpperCase();
    private final List<Mood> moods;
    private final Context context;
    private onClickListener onClickListener;

    public MoodTopAdapter(Context context, List<Mood> moods, onClickListener onClickListener) {
        this.context = context;
        this.moods = moods;
        this.onClickListener = onClickListener;
    }

    @Override
    public MoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_mood_item, parent, false);
        MoodsViewHolder holder = new MoodsViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MoodsViewHolder holder, final int position) {
        holder.img.setImageResource(moods.get(position).getImg());

        if (onClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickListener(holder.itemView, position);
                }
            });
        }
    }

    public interface onClickListener {
        void onClickListener(View view, int idx);
    }

    @Override
    public int getItemCount() {
        return this.moods != null ? this.moods.size() : 0;
    }

    public static class MoodsViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public MoodsViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.img);
        }
    }
}
