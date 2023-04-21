package com.example.androidphotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class ImageLoader extends AsyncTask<Uri, Void, Bitmap> {

    private WeakReference<ImageView> imageViewReference;
    private Context context;
    private Uri uri;

    public ImageLoader(ImageView imageView, Context context) {
        imageViewReference = new WeakReference<>(imageView);
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Uri... uris) {
        uri = uris[0];
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}

