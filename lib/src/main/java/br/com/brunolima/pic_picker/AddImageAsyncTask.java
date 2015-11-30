package br.com.brunolima.pic_picker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import br.com.brunolima.pic_picker.listener.PicResultListener;

/**
 * Created by brunodles on 10/11/14.
 */
public class AddImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = "AddImageAsyncTask";

    private static final int DEFAULT_TARGET_SIZE_IN_KBYTES = 300; // 300kbytes
    private static final int DEFAULT_TARGET_WIDTH = 600;
    private static final int DEFAULT_TARGET_HEIGHT = 600;
    private static final Bitmap.CompressFormat DEFAULT_FILE_FORMAT = Bitmap.CompressFormat.JPEG;

    protected ImageView view;
    protected Uri url;

    private int quality;
    private PicResultListener listener;

    public AddImageAsyncTask(ImageView view, Uri url) {
        this.view = view;
        this.url = url;
        Log.d(TAG, "AddFileAsyncTask url " + url);
    }

    public AddImageAsyncTask setListener(PicResultListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        view.setAlpha(0.3f);
        loadTempImage();
    }

    protected void loadTempImage() {
//        PicassoUtil.with(view)
//                .fit()
//                .load(url);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            Bitmap bitmap;
//          bitmap = getScaledBitmapFromFile();
//            logBitmapSizes("scaledFile", bitmap);
//            if (bitmap == null) {
//            bitmap = getScaledBitmapFromStream();
//            logBitmapSizes("scaledFromStream", bitmap);
//            }
//            if (bitmap == null) {
                bitmap = getBitmapFromStream();
                logBitmapSizes("fromStream", bitmap);
//            }
            if (bitmap == null)
                return null;
            return compress(bitmap);
        } catch (Exception e) {
            Log.e(TAG, "doInBackground ", e);
        }
        return null;
    }

    private void logBitmapSizes(String typeString, Bitmap bitmap) {
        if (bitmap != null)
            Log.d(TAG, String.format("doInBackground %s w = %s h = %s\n", typeString, bitmap.getWidth(), bitmap.getHeight()));
    }

    private Bitmap getBitmapFromStream() throws IOException {
        InputStream stream = getContext().getContentResolver().openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        stream.close();
        return bitmap;
    }

    private Context getContext() {
        return view.getContext();
    }

    private Bitmap getScaledBitmapFromStream() throws IOException {
        InputStream stream = null;
        try {
            stream = getContext().getContentResolver().openInputStream(url);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, null, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            Log.d(TAG, String.format("getScaledBitmapFromStream w = %s h = %s\n", photoW, photoH));

            int scaleFactor = Math.min(photoW / DEFAULT_TARGET_WIDTH, photoH / DEFAULT_TARGET_HEIGHT);
            Log.d(TAG, "doInBackground scaleFactor " + scaleFactor);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            stream = getContext().getContentResolver().openInputStream(url);
            return BitmapFactory.decodeStream(stream, null, bmOptions);
        } catch (IOException e) {
            Log.e(TAG, "getScaledBitmapFromStream ", e);
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    private Bitmap getScaledBitmapFromFile() {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            String pathName = url.toString();
            BitmapFactory.decodeFile(pathName, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / DEFAULT_TARGET_WIDTH, photoH / DEFAULT_TARGET_HEIGHT);
            Log.d(TAG, "doInBackground scaleFactor " + scaleFactor);
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            return BitmapFactory.decodeFile(pathName, bmOptions);
        } catch (Exception e) {
            Log.e(TAG, "getScaledBitmapFromFile ", e);
        }
        return null;
    }

    private Bitmap compress(Bitmap bitmap) throws Exception {
        quality = 100;
        while (quality >= 50) {
            Log.d(TAG, "compress; try to create parse file with quality " + quality);
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(DEFAULT_FILE_FORMAT, quality, out);
                int size = out.size() / 1024;
                Log.d(TAG, "compress size " + size);
                if (size > DEFAULT_TARGET_SIZE_IN_KBYTES && quality >= 60) {
                    quality -= 10;
                    continue;
                }
                return bitmap;
            } catch (Exception e) {
                Log.e(TAG, "compress; error on create parse file with quality " + quality, e);
            }
            quality -= 10;
        }
        throw new Exception("Can't create parse file.");
    }

    protected final String getFileName() {
        return String.format("file%s", System.currentTimeMillis());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null) {
            view.setAlpha(0f);
            view.setImageResource(android.R.color.white);
            view.setAlpha(1f);
        } else {
            view.setImageBitmap(bitmap);
            Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
            view.setAlpha(1f);
            view.startAnimation(animation);
        }
        if (listener != null) listener.onPictureResult(bitmap);
    }
}
