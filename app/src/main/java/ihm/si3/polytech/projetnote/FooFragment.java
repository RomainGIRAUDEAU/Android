package ihm.si3.polytech.projetnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FooFragment extends Fragment {


    private static final String ARG_SECTION_NUMBER = "1";


    public FooFragment() {


    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FooFragment newInstance() {
        FooFragment fragment = new FooFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation. 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_formulaire, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
    }
}