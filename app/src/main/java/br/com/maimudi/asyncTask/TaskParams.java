package br.com.maimudi.asyncTask;

import android.content.Context;

/**
 * Created by Ronan.lima on 01/10/16.
 */
public class TaskParams {
    private Context context;
    private ImageLoaderExternals.OnBitmapLoaded listener;
    private String urlImg;

    public TaskParams(Context context, ImageLoaderExternals.OnBitmapLoaded listener, String urlImg) {
        setContext(context);
        setListener(listener);
        setUrlImg(urlImg);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ImageLoaderExternals.OnBitmapLoaded getListener() {
        return listener;
    }

    public void setListener(ImageLoaderExternals.OnBitmapLoaded listener) {
        this.listener = listener;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}
