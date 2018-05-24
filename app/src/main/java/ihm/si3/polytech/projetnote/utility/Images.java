package ihm.si3.polytech.projetnote.utility;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by laodakes on 25/05/18.
 */

public class Images {
    private List<Bitmap> images;
    private ImageView imageView;

    public Images(ImageView imageView){
        images = new ArrayList<>();
        this.imageView = imageView;
    }

    public boolean refresh(int pos) {
        if(pos < images.size() && pos >= 0) {
            imageView.setImageBitmap(images.get(pos));
            return true;
        }
        return false;
    }

    public int addBitmap(Bitmap bitmap) {
        images.add(bitmap);
        refresh(images.size()-1);
        return images.size()-1;
    }

    public List<Bitmap> getImages() {
        return images;
    }

}