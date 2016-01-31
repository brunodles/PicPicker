package com.github.brunodles.pic_picker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.github.brunodles.pic_picker.listener.PicResultListener;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by brunodles on 10/11/14.
 */
class AddImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = "AddImageAsyncTask";

    protected final Context context;
    protected final Uri url;
    private final PicResultListener listener;

    public AddImageAsyncTask(Context context, Uri url, PicResultListener listener) {
        this.context = context;
        this.url = url;
        this.listener = listener;
        Log.d(TAG, "AddFileAsyncTask url " + url);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            return getBitmapFromStream();
        } catch (Exception e) {
            Log.e(TAG, "doInBackground ", e);
        }
        return null;
    }

    private Bitmap getBitmapFromStream() throws IOException {
        InputStream stream = context.getContentResolver().openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        if (stream != null) stream.close();
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (listener != null) listener.onPictureResult(bitmap);
    }
}
