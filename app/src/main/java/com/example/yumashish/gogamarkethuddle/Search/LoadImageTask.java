package com.example.yumashish.gogamarkethuddle.Search;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by yumashish on 11/4/15.
 */
public class LoadImageTask extends AsyncTask<Void, Integer, Bitmap> {
    public static final String TAG = "GOGA_LOAD_IMAGE_TASK";
    public String url;
    public int width, height, border;
    private ImageLoadedHandler mHandler;

    public LoadImageTask(String url, int width, int height, int border) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.border = border;
    }

    public void SetImageLoadedHandler(ImageLoadedHandler handler) {
        mHandler = handler;
    }

    public LoadImageTask(String url, int width, int height, int border, ImageLoadedHandler handler) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.mHandler = handler;
        this.border = border;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap image = null;
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            image = BitmapFactory.decodeStream(is);
            if(width > 0 && height > 0)
                image = addWhiteBorder(getResizedBitmap(image, width,height), border);
        } catch (Exception ex) {
            Log.e(TAG, "Problem loading the profile image at " + url);
            ex.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        if(mHandler != null) {
            mHandler.Handle(image);
        }
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
        if(borderSize < 1)
            return bmp;
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }
}
