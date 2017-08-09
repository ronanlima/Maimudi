package br.com.maimudi.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by brunolemgruber on 22/04/16.
 */
public class SquareView extends ImageView {

    public SquareView(Context context) {
        super(context);
    }

    public SquareView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SquareView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
