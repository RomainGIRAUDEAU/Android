package ihm.si3.polytech.projetnote.visualisationincident;

import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.utility.Mishap;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

/**
 * Created by user on 26/03/2018.
 */

public class NewsGridFragment extends android.support.v4.app.Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static NewsGridFragment fragment;
    private FirebaseFunctions mFunctions;

    /**
     * mishap  selected to get an order
     */
    public List<Mishap> selectedMishap;

    private List<Mishap> articleList;
    MyRecyclerAdapter mAdapter;
    private Location location;

    public NewsGridFragment() {
        articleList = new ArrayList<>();
        selectedMishap = new ArrayList<>();


    }

    public static NewsGridFragment getInstance() {
        return fragment;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NewsGridFragment newInstance() {
        fragment = new NewsGridFragment();
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
    public void onActivityCreated(Bundle savedInstanceState) {        // [START initialize_functions_instance]
        mFunctions = FirebaseFunctions.getInstance();


        super.onActivityCreated(savedInstanceState);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("mishap");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                articleList = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Mishap climate = postSnapshot.getValue(Mishap.class);
                    articleList.add(climate);
                }

                mAdapter = new MyRecyclerAdapter(articleList);
                setupRecyclerView();

            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("firebase error :" + firebaseError.getDetails());
            }
        });

        // [START call_add_message]
        addMessage("test")
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            // [START_EXCLUDE]
                            Log.w(TAG, "addMessage:onFailure", e);
                            //showSnackbar("An error occurred.");
                            return;
                            // [END_EXCLUDE]
                        }

                        // [START_EXCLUDE]
                        String result = task.getResult();
                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        // mMessageOutputField.setText(result);
                        // [END_EXCLUDE]
                    }
                });




    }

    /**
     * See the adapter in the sample project for a click listener implementation. Click listeners
     * aren't provided by this library.
     */


    private void setupRecyclerView() {


        RecyclerView recyclerView = getActivity().findViewById(R.id.listArticle);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
        final SwipeController swipeController = new SwipeController(new SwipeControllerAction() {
            @Override
            public void onLeftClicked(int position) {
                Toast.makeText(getContext(), "your select mishap " + articleList.get(position), Toast.LENGTH_LONG).show();
                //  selectedMishap.add(articleList.get(position));


            }

            @Override
            public void onRightClicked(int position) {
                //selectedMishap.remove(position);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });


    }

    public List<Mishap> getSelectedMishap() {
        return mAdapter.getSelectedMishap();
    }


    // [START function_add_message]
    private Task<String> addMessage(String text) {
        // Create the arguments to the callable function, which is just one string
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }



}
