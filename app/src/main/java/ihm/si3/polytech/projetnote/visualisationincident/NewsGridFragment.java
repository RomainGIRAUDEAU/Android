package ihm.si3.polytech.projetnote.visualisationincident;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.utility.Mishap;

/**
 * Created by user on 26/03/2018.
 */

public class NewsGridFragment extends android.support.v4.app.Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private List<Mishap> articleList;

    public NewsGridFragment() {
        articleList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Mishap climate = postSnapshot.getValue(Mishap.class);
                    articleList.add(climate);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("firebase error :" + firebaseError.getDetails());
            }
        });

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



        GridView gridView = getView().findViewById(R.id.listArticle);
        gridView.setAdapter(new NewCustomAdapter(this.getContext(), articleList));


    }


}
