package ihm.si3.polytech.projetnote.visualisationincident;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.login.StoreUsers;
import ihm.si3.polytech.projetnote.notused.DownloadImagesTask;
import ihm.si3.polytech.projetnote.utility.Mishap;


/**
 * Created by user on 08/04/2018.
 */

public class NewCustomAdapter extends ArrayAdapter<Mishap> {

    public NewCustomAdapter(Context context, List<Mishap> articles) {
        super(context, R.layout.customlayout, articles);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customlayout, null);
        }

        TextView titleAricle = convertView.findViewById(R.id.card_title);
        TextView description = convertView.findViewById(R.id.card_description);
        TextView author = convertView.findViewById(R.id.person_username);
        ImageView imageArticle = convertView.findViewById(R.id.card_mishap);
        ImageView imageAuthor = convertView.findViewById(R.id.person_picture);


        Mishap currentMishap = getItem(position);
        titleAricle.setText(currentMishap.getTitle());
        description.setText(currentMishap.getDescription());
        author.setText(currentMishap.getAuthor());


        DownloadImagesTask downloadImagesTask = new DownloadImagesTask(imageArticle);
        downloadImagesTask.execute(StoreUsers.getUrlPicture());

       /* if( getItem(position).getMedia() == Priority.CRITICAL) {

            DownloadImagesTask downloadImagesTask = new DownloadImagesTask(imageArticle);
            downloadImagesTask.execute(getItem(position).getUrl());
        }
        else if( getItem(position).getMedia() == Media.VIDEO){ // get the picture from youtube
            Map<String, String> urlParameters = null;

            try {
                urlParameters = splitQuery(new URL(currentArticle.getUrl()));
            } catch (MalformedURLException|UnsupportedEncodingException err) {
                System.out.println("malformed video URL in database: " + currentArticle.getUrl());

            }
            String videoID = urlParameters.get("v");
            if (videoID == null) {
                System.out.println("non-YouTube video URL in database");

            }
            String image_url = "https://img.youtube.com/vi/" + videoID + "/mqdefault.jpg";
            DownloadImagesTask downloadImagesTask = new DownloadImagesTask(imageArticle);
            downloadImagesTask.execute(image_url);
            imageArticle.setVisibility(View.VISIBLE);


        }
        */


        return convertView;
    }


}
