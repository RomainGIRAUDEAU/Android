package ihm.si3.polytech.projetnote.notused;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 08/04/2018.
 */

public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {
    Map<String, Bitmap> storedImage;
    private ImageView imageView;

    public DownloadImagesTask(ImageView imageView) {
        this.imageView = imageView;
        storedImage = new HashMap<>();
    }


    @Override
    protected Bitmap doInBackground(String... url) {
        Bitmap imageDownload = storedImage.get(url[0]);
        if (imageDownload != null) {
            return imageDownload;
        }

        InputStream imageStream;
        try {
            imageStream = new BufferedInputStream(new URL(url[0]).openStream());
        } catch (IOException err) {
            throw new RuntimeException("error loading Internet image");
        }
        Bitmap loadedBitmap = BitmapFactory.decodeStream(imageStream);
        storedImage.put(url[0], loadedBitmap);
        return loadedBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }


}
