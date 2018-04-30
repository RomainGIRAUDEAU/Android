package ihm.si3.polytech.projetnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by user on 08/04/2018.
 */

public class NewCustomAdapter extends ArrayAdapter<Mishap> {

    public NewCustomAdapter(Context context, List<Mishap> articles) {
        super(context, 0, articles);


    }

    /**
     * URI parameters parser from https://stackoverflow.com/a/13592567/5248987
     *
     * @param url The URL to parse
     * @return A Map containing the parameter names and values
     * @throws UnsupportedEncodingException If the URL encoding isn't supported
     * @author Pr0gr4mm3r @stackoverflow.com
     */
    private static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customlayout, null);
        }

        ImageView imageArticle = convertView.findViewById(R.id.article_image);
        TextView titleAricle = convertView.findViewById(R.id.titleArticle);
        Mishap currentMishap = getItem(position);
        titleAricle.setText(currentMishap.getTitle());

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
        TextView descr = (TextView) convertView.findViewById(R.id.descriptionArticle);
        descr.setText(getItem(position).getText());


    */

        return convertView;
    }


}
