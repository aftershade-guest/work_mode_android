package com.aftershade.workmode.HelperClasses.Images;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageWareHouse implements com.squareup.picasso.Callback {

    private final static String TAG = "ImageWareHouse";
    private final String mDirrectory;
    private final String mFileName;
    private final ImageView mContainer;
    Bitmap bitmap;

    //App mApplication;


    public ImageWareHouse(String mDirrectory, String mFileName, ImageView mContainer) {
        this.mDirrectory = mDirrectory;
        this.mFileName = mFileName;
        this.mContainer = mContainer;
        this.getStorageDir();
    }

    private File getStorageDir() {

        File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES.concat(this.mDirrectory));
        if (!file.mkdir()) {

        }

        return file;

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    public void onSuccess() {

        if (this.isExternalStorageWritable()) {
            bitmap = ((BitmapDrawable) this.mContainer.getDrawable()).getBitmap();

            new task().execute();

        } else {

        }

    }

    @Override
    public void onError(Exception e) {

    }

    private class task extends AsyncTask<Void, Void, File> {

        @Override
        protected File doInBackground(Void... voids) {
            File file = null;

            file = new File(ImageWareHouse.this.getStorageDir().getPath().concat("/").
                    concat(ImageWareHouse.this.mFileName));


            try {

                file.createNewFile();
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return file;

        }
    }
}
