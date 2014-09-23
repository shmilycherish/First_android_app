package com.cherish.myduban;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cyang on 9/23/14.
 */
public class ImageLoader {

    public interface ImageLoaderListener {
        void onImageLoaded(Bitmap bitmap);
    }


    private final static ConcurrentHashMap<String, Bitmap> cache = new ConcurrentHashMap<String, Bitmap>();

    public static void loadImage(final String urlStr, final ImageLoaderListener imageLoaderListener) {
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    if (cache.contains(urlStr)) {
                        return cache.get(urlStr);
                    } else {

                        URL url = new URL(strings[0]);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        final InputStream inputStream = connection.getInputStream();

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds=true;
                        BitmapFactory.decodeStream(inputStream, null, options);
                        options.inJustDecodeBounds=false;
                        int scale = (int)(options.outHeight / (float)108);
                        if(scale <=0) {
                            scale = 1;
                        }
                        options.inSampleSize = scale;
                        HttpURLConnection newConnection = (HttpURLConnection) url.openConnection();
                        Bitmap realBitmap = BitmapFactory.decodeStream(newConnection.getInputStream(), null, options);

                        inputStream.close();
                        connection.disconnect();
                        cache.put(urlStr, realBitmap);
                        return realBitmap;

                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (imageLoaderListener != null) {
                    imageLoaderListener.onImageLoaded(bitmap);
                }
            }
        }.execute(urlStr);
    }

}
