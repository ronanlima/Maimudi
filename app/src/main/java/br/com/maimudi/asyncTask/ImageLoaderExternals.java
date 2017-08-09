package br.com.maimudi.asyncTask;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ronan.lima on 01/10/16.
 */
public class ImageLoaderExternals extends AsyncTask<TaskParams, Void, Bitmap> {
    private OnBitmapLoaded listener;
    private Bitmap bitmap;

    @Override
    protected Bitmap doInBackground(TaskParams... taskParamses) {
        TaskParams params = taskParamses[0];
        this.listener = params.getListener();

        try {
            bitmap = Glide.with(params.getContext()).load(params.getUrlImg()).asBitmap().into(-1,-1).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        listener.returnBitmap(bitmap);
    }

    public interface OnBitmapLoaded extends Serializable {
        void returnBitmap(Bitmap bitmap);
    }
}
