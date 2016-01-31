package com.github.brunodles.compressor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * This class will help you to compress your bitmap.
 * When you get images from the others that image can have huge sizes, and sometimes we don't need
 * all that quality. Then you can use this class to help you with that. Just inform the wanted
 * image size (in kbytes).
 * <p/>
 * Created by bruno on 31/01/16.
 */
public class BitmapCompressor extends AsyncTask<Bitmap, Void, Bitmap[]> {

    private static final String TAG = "BitmapCompressor";

    private static final Bitmap.CompressFormat DEFAULT_FILE_FORMAT = Bitmap.CompressFormat.JPEG;

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    private int targetSize;

    /**
     * @param targetSizeInKbytes the wanted image size in kbytes.
     */
    public BitmapCompressor(int targetSizeInKbytes) {
        this.targetSize = targetSizeInKbytes;
    }

    private Bitmap compress(Bitmap bitmap) {
        int quality = 90;
        int interactions = 0;
        while (quality >= 0) {
            try {
                Log.d(TAG, "compress: quality " + quality);
                out.reset();
                bitmap.compress(DEFAULT_FILE_FORMAT, quality, out);
                int currentSize = out.size() / 1024;
                Log.d(TAG, "compress: currentSize " + currentSize);
                if (currentSize <= targetSize)
                    break;
                int nextQuality = ((targetSize * quality) / currentSize);
                if (nextQuality == quality)
                    break;
                else
                    quality = nextQuality;
            } catch (Exception e) {
                Log.e(TAG, "Error compressing bitmap " + quality, e);
            }
            interactions++;
            if (interactions > 5)
                break;
        }
        if (out.size() > 0)
            return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
        return bitmap;
    }

    @Override
    protected Bitmap[] doInBackground(Bitmap... params) {
        Bitmap[] bitmaps = new Bitmap[params.length];
        for (int i = 0; i < params.length; i++) {
            bitmaps[i] = compress(params[i]);
        }
        return bitmaps;
    }
}