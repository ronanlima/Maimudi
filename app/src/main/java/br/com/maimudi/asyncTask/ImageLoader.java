package br.com.maimudi.asyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;

import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.activity.MediaActivity;

/**
 * Created by brunolemgruber on 22/04/16.
 */
public class ImageLoader extends AsyncTask<List<String>, Void, List<Bitmap>> {

    private List<Bitmap> bitmapList;
    private MediaActivity ctx;
    private SimpleArcDialog simpleArcDialog;

    public ImageLoader(MediaActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        int[] colors = {R.color.colorPrimaryDark, R.color.colorPrimary};

        ArcConfiguration configuration = new ArcConfiguration(ctx);
        configuration.setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
        configuration.setText("Carregando as fotos");
        configuration.setColors(colors);

        simpleArcDialog = new SimpleArcDialog(ctx);
        simpleArcDialog.setConfiguration(configuration);
        simpleArcDialog.show();
    }

    @Override
    protected List<Bitmap> doInBackground(List<String>... params) {

       bitmapList = new ArrayList<>();
       for (int i = 0; i < params[0].size() ; i++){
            bitmapList.add(decodeSampledBitmapFromFile(params[0].get(i),300,300));
        }

        return bitmapList;
    }

    @Override
    protected void onPostExecute(List<Bitmap> bitmaps) {
        super.onPostExecute(bitmaps);

        ctx.result(bitmaps);

        simpleArcDialog.dismiss();
    }

    public static Bitmap decodeSampledBitmapFromFile(String f, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(f, options);

        // Calculate inSampleSize
        // Raw height and width of background
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }

        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // Correct rotation
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = exifToDegrees(rotation);
        Matrix matrix = new Matrix();
        if (rotation != 0f) {
            matrix.preRotate(rotationInDegrees);
        }

        Bitmap output = BitmapFactory.decodeFile(f, options);
        if (output != null) {
            return Bitmap.createBitmap(output, 0, 0, output.getWidth(), output.getHeight(), matrix, false);
        } else {
            return null;
        }
    }

    public static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
}
