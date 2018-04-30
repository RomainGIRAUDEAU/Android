package ihm.si3.polytech.projetnote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 26/03/2018.
 */

public class NewsGridFragment extends android.support.v4.app.Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private List<Mishap> articleList;

    public NewsGridFragment() {
        articleList = new ArrayList<>();

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NewsGridFragment newInstance() {
        NewsGridFragment fragment = new NewsGridFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_grid, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NewsDBHelper newsDBHelper = new NewsDBHelper(this.getContext());
        try {
            newsDBHelper.createDataBase();
            newsDBHelper.openDataBase();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        articleList = newsDBHelper.getAllArticles();
        newsDBHelper.close();

        GridView gridView = getView().findViewById(R.id.listArticle);
        gridView.setAdapter(new NewCustomAdapter(this.getContext(), articleList));


    }


}
