package ihm.si3.polytech.projetnote.creationincident;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.login.StoreUsers;
import ihm.si3.polytech.projetnote.utility.Mishap;
import ihm.si3.polytech.projetnote.utility.Priority;

public class MishapCreator extends Fragment {


    private static final String ARG_SECTION_NUMBER = "1";

    private DatabaseReference databaseReference;


    public MishapCreator() {


    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MishapCreator newInstance() {
        MishapCreator fragment = new MishapCreator();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation. 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.recipe, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button button = getActivity().findViewById(R.id.valider);
        final Spinner spinner = getActivity().findViewById(R.id.SpinnerFeedbackType);

        button.setOnClickListener(new View.OnClickListener() // do something on click
        {
            public void onClick(View v) {

                TextView title = getActivity().findViewById(R.id.title);
                TextView description = getActivity().findViewById(R.id.descriptionArticle);
                Mishap mishap = new Mishap();
                mishap.setTitle(title.getText().toString().trim());
                mishap.setDescription(description.getText().toString().trim());
                mishap.setPriority(Priority.valueOf(spinner.getSelectedItem().toString()));
                mishap.setAuthor(StoreUsers.getUserName());
                mishap.setUrlPicture(StoreUsers.getUrlPicture());

                databaseReference = FirebaseDatabase.getInstance().getReference("mishap");


                databaseReference.child(mishap.getTitle()).setValue(mishap);
                Toast.makeText(getContext(), "Information Save", Toast.LENGTH_LONG).show();


            }
        });
    }


    private void saveMishap() {

    }



}