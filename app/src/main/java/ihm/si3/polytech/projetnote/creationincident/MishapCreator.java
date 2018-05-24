package ihm.si3.polytech.projetnote.creationincident;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.login.StoreUsers;
import ihm.si3.polytech.projetnote.utility.Mishap;
import ihm.si3.polytech.projetnote.utility.Priority;

import static android.app.Activity.RESULT_OK;

public class MishapCreator extends Fragment {


    static final int REQUEST_IMAGE_CAPTURE = 111;
    private DatabaseReference databaseReference;
    private StorageReference mStorage;
    private ImageView imageView;
    private Bitmap imageBitmap;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button button = getActivity().findViewById(R.id.valider);
        Button buttonPicture = getActivity().findViewById(R.id.takePicture);
        imageView = getActivity().findViewById(R.id.imageView);
        final Spinner spinner = getActivity().findViewById(R.id.SpinnerFeedbackType);

        mStorage = FirebaseStorage.getInstance().getReference();

        buttonPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }

            private void dispatchTakePictureIntent() {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

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

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                mishap.setImageUrl(imageEncoded);

                databaseReference = FirebaseDatabase.getInstance().getReference("mishap");

                databaseReference.child(mishap.getTitle()).setValue(mishap);
                Toast.makeText(getContext(), "Information Save", Toast.LENGTH_LONG).show();

            }
        });
    }


    private void saveMishap() {

    }



}