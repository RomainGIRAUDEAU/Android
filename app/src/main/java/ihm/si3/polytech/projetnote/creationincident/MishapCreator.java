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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ihm.si3.polytech.projetnote.MainActivity;
import ihm.si3.polytech.projetnote.Manifest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.io.ByteArrayOutputStream;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.login.StoreUsers;
import ihm.si3.polytech.projetnote.utility.Batiment;
import ihm.si3.polytech.projetnote.utility.Images;
import ihm.si3.polytech.projetnote.utility.Mishap;
import ihm.si3.polytech.projetnote.utility.Priority;
import ihm.si3.polytech.projetnote.visualisationincident.MyRecyclerAdapter;

import static android.app.Activity.RESULT_OK;

public class MishapCreator extends Fragment implements AdapterView.OnItemSelectedListener {


    private static final String ARG_SECTION_NUMBER = "1";
    static final int REQUEST_IMAGE_CAPTURE = 111;
    private DatabaseReference databaseReference;
    private Bitmap imageBitmap;
    private FusedLocationProviderClient mFusedLocationClient;
    private int imagePos;
    private Images images;
    Spinner s1,s2;
    List<Batiment> batList;
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
        setBatList();
        return inflater.inflate(R.layout.declaration_view, parent, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imagePos = images.addBitmap(imageBitmap);
        }
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
        ImageButton buttonPicture = getActivity().findViewById(R.id.takePicture);
        images = new Images((ImageView)getActivity().findViewById(R.id.imageView));
        imagePos = 0;
        ImageButton buttonPrevious = getActivity().findViewById(R.id.previous);
        ImageButton buttonNext = getActivity().findViewById(R.id.next);
        Button buttonGPS = getActivity().findViewById(R.id.btnGPS);
        final Spinner spinner = getActivity().findViewById(R.id.SpinnerFeedbackType);
        s1 = getActivity().findViewById(R.id.spinnerBatiment);
        s2 = getActivity().findViewById(R.id.spinnerSalle);
        s1.setOnItemSelectedListener(this);

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
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(images.refresh(imagePos-1)) --imagePos;
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(images.refresh(imagePos+1)) ++imagePos;
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
                                //s1.setSelection(getPosition(getBestBat(mLocation)));
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

                for(Bitmap bitmap : images.getImages()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                    mishap.addImage(imageEncoded);
                }

                databaseReference = FirebaseDatabase.getInstance().getReference("mishap");
                String id = databaseReference.push().getKey();
                databaseReference.child(id).setValue(mishap);
                Toast.makeText(getContext(), "Information Save", Toast.LENGTH_LONG).show();

            }
        });
    }
    void setBatList(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Batiment");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                batList = new ArrayList<Batiment>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Batiment batiment= postSnapshot.getValue(Batiment.class);
                    batList.add(batiment);
                }
                setSpinnerBat();

            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("firebase error :" + firebaseError.getDetails());
            }
        });
    }

    private void setSpinnerBat() {
        List<String> list = new ArrayList<String>();
        for(Batiment bat:batList){
            list.add(bat.getName());
        }
        list.add("Autre");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.notifyDataSetChanged();
        s1.setAdapter(dataAdapter);
        if(mLocation!=null) {
            s1.setSelection(getPosition(getBestBat(mLocation)));
        }
    }

    private int getPosition(String bestBat) {
        for(int i=0;i<batList.size();i++){
            if(bestBat.equals(batList.get(i).getName())){
                return i;
            }
        }
        return 0;
    }

    private String getBestBat(Location l) {
        double longitude = l.getLongitude();
        double latitude=l.getLatitude();
        double batLat1,batLat2,batLong1,batLong2;
        for(Batiment bat:batList){
            batLat1=bat.getLatitude1();batLat2=bat.getLatitude2();batLong1=bat.getLongitude1();batLong2=bat.getLongitude2();
            if(((batLat1<latitude&&latitude<batLat2||batLat1>latitude&&latitude>batLat2)&&(batLong1<longitude&&longitude<batLong2||batLong1>longitude&&longitude>batLong2))){
                return bat.getName();
            }
        }
        return "Autre";

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


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        String sp1= String.valueOf(s1.getSelectedItem());
        if(sp1.contentEquals("Income")) {
            List<String> list = new ArrayList<String>();
            list.add("Salary");//You should add items from db here (first spinner)

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            s2.setAdapter(dataAdapter);
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }
}