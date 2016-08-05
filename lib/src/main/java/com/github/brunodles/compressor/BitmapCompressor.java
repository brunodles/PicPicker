package com.github.brunodles.compressor;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * This class will help you to compress your bitmap.
 * When you get images from the others that image can have huge sizes, and sometimes we don't need
 * all that. Then you can use this class to help you with that. Just inform the wanted
 * image size (in kbytes) and how you want to compress it.
 * <p>
 * Look {@link Compressor} and it's implementations {@link QualityCompressor} and {@link SizeCompressor}
 * <p>
 * Created by bruno on 31/01/16.
 */
public class BitmapCompressor extends AsyncTask<Bitmap, Void, Bitmap[]> {

    private static final String TAG = "BitmapCompressor";

    private final Compressor compressor;
    private final int targetSize;

    /**
     * @param compressor         the script that will be used to compress the image {@link Compressor}
     * @param targetSizeInKbytes the wanted image size in kbytes.
     */
    public BitmapCompressor(Compressor compressor, int targetSizeInKbytes) {
        this.compressor = compressor;
        this.targetSize = targetSizeInKbytes;
    }

    /**
     * This use the {@link QualityCompressor} to compress the image.
     *
     * @param targetSizeInKbytes the wanted image size in kbytes.
     */
    public BitmapCompressor(int targetSizeInKbytes) {
        this(new QualityCompressor(), targetSizeInKbytes);
    }

    @Override
    protected Bitmap[] doInBackground(Bitmap... params) {
        Bitmap[] bitmaps = new Bitmap[params.length];
        for (int i = 0; i < params.length; i++) {
            bitmaps[i] = compressor.compress(params[i], targetSize);
        }
        return bitmaps;
    }
}