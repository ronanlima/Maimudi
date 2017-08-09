package br.com.maimudi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import br.com.maimudi.R;

/**
 * Created by brunolemgruber on 22/04/16.
 */
public class SquareViewAdapter extends BaseAdapter {

    private List<Bitmap> items = new ArrayList<Bitmap>();
    private LayoutInflater inflater;

    public SquareViewAdapter(Context context, List<Bitmap> items)
    {
        inflater = LayoutInflater.from(context);

        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ImageView picture;

        if(v == null)
        {
            v = inflater.inflate(R.layout.gridview_item, parent, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
        }

        picture = (ImageView)v.getTag(R.id.picture);

        Bitmap item = (Bitmap)getItem(position);
        picture.setImageBitmap(item);

//        BitmapFactory.Options opts = new BitmapFactory.Options();
//        opts.inSampleSize = 4;
//        Bitmap newBitmap = BitmapFactory.decodeFile(item, opts);

//        picture.setImageBitmap(decodeSampledBitmapFromFile(item,300,300));

        return v;
    }

}
