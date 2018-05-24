package ihm.si3.polytech.projetnote.creationincident;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.login.StoreUsers;
import ihm.si3.polytech.projetnote.utility.Mishap;
import ihm.si3.polytech.projetnote.utility.Priority;

import static android.app.Activity.RESULT_OK;

public class MishapCreator extends Fragment  {


    private static final String ARG_SECTION_NUMBER = "1";
    static final int REQUEST_IMAGE_CAPTURE = 111;
    private DatabaseReference databaseReference;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private FusedLocationProviderClient mFusedLocationClient;
    Location mLocation;

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
        return inflater.inflate(R.layout.declaration_view, parent, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    private void encodeBitmapAndSaveToFirebase(Bitmap imageBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("imageUrl");
        ref.setValue(imageEncoded);
    }


    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button button = getActivity().findViewById(R.id.valider);
        Button buttonPicture = getActivity().findViewById(R.id.takePicture);
        imageView = getActivity().findViewById(R.id.imageView);
        Button buttonGPS = getActivity().findViewById(R.id.btnGPS);
        final Spinner spinner = getActivity().findViewById(R.id.SpinnerFeedbackType);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.

                            setLocationText(location);
                        }
                    });
        }

        buttonPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onLaunchCamera();
            }

            private void dispatchTakePictureIntent() {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        buttonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                setLocationText(location);
                            }
                        });

            }
        });

        button.setOnClickListener(new View.OnClickListener() // do something on click
        {
            public void onClick(View v) {

                TextView title = getActivity().findViewById(R.id.title1);
                TextView description = getActivity().findViewById(R.id.descriptionArticle);
                Mishap mishap = new Mishap();
                mishap.setTitle(title.getText().toString().trim());
                mishap.setDescription(description.getText().toString().trim());
                mishap.setPriority(Priority.valueOf(spinner.getSelectedItem().toString()));
                mishap.setAuthor(StoreUsers.getUserName());
                String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date());
                mishap.setDate(date);

                if(mLocation!=null) {
                    mishap.setxPos(mLocation.getLatitude());
                    mishap.setyPos(mLocation.getLongitude());
                }else{
                    mishap.setxPos((double) 0);
                    mishap.setyPos((double) 0);
                }
                mishap.setUrlPicture(StoreUsers.getUrlPicture());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                mishap.setImageUrl(imageEncoded);

                databaseReference = FirebaseDatabase.getInstance().getReference("mishap");
                String id = databaseReference.push().getKey();
                databaseReference.child(id).setValue(mishap);
                Toast.makeText(getContext(), "Information Save", Toast.LENGTH_LONG).show();

            }
        });
    }


    private void saveMishap() {

    }

    private void setLocationText(Location l){
        TextView textGPS= getActivity().findViewById(R.id.textGPS);
        if (l!=null){
            this.mLocation=l;
            textGPS.setText("Longitude : "+l.getLongitude()+"/Latitude : "+l.getLatitude());
        }else{
            textGPS.setText("Longitude : N /Latitude : N");
        }
    }






}