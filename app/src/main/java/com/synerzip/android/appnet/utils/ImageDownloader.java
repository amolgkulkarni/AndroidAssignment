package com.synerzip.android.appnet.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.synerzip.android.appnet.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Creates Bitmap from image url in background thread.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "AppNetPosts";
    private ImageView mBmImage;
    private Context mContext;
    private String mFileName;

    public ImageDownloader(ImageView bmImage, Context context, String fileName) {
        this.mBmImage = bmImage;
        this.mContext = context;
        this.mFileName = fileName;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon = null;
        if (new File(mContext.getCacheDir(), this.mFileName).exists()) {
            // Get from Cache
            mIcon = BitmapFactory.decodeFile(new File(mContext.getCacheDir(),
                    this.mFileName).getPath());
        } else {
            try {
                mIcon = BitmapFactory.decodeStream(new java.net.URL(url).openStream());

                // Save to Cache
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(new File(mContext.getCacheDir(), this.mFileName));
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.toString(), e);
                }
                //if the file couldn't be saved
                if(!mIcon.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                    Log.e(TAG, "The image could not be saved: " + this.mFileName);
                    mIcon = BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.placeholder);
                }

                fos.flush();
                fos.close();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        mBmImage.setImageBitmap(result);
    }
}
