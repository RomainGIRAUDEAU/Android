package ihm.si3.polytech.projetnote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.provider.MediaStore;
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

class DownloadVideoTask extends AsyncTask<String,Void,MediaStore.Video> {
    private ImageView imageView;

    Map<String,MediaStore.Video> storedImage;
    DownloadVideoTask(ImageView imageView){
        this.imageView = imageView;
        storedImage = new HashMap<>();
    }



    @Override
    protected MediaStore.Video doInBackground(String... url) {
      return null;
    }



}
