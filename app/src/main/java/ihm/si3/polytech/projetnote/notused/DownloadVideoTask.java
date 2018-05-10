package ihm.si3.polytech.projetnote.notused;

import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 08/04/2018.
 */

class DownloadVideoTask extends AsyncTask<String, Void, MediaStore.Video> {
    Map<String, MediaStore.Video> storedImage;
    private ImageView imageView;

    DownloadVideoTask(ImageView imageView) {
        this.imageView = imageView;
        storedImage = new HashMap<>();
    }


    @Override
    protected MediaStore.Video doInBackground(String... url) {
        return null;
    }


}
