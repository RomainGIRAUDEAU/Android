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
import android.widget.EditText;
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
import ihm.si3.polytech.projetnote.utility.Mishap;
import ihm.si3.polytech.projetnote.utility.Priority;
import ihm.si3.polytech.projetnote.utility.Salle;
import ihm.si3.polytech.projetnote.visualisationincident.MyRecyclerAdapter;

import static android.app.Activity.RESULT_OK;

public class MishapCreator extends Fragment implements AdapterView.OnItemSelectedListener {


    private static final String ARG_SECTION_NUMBER = "1";
    static final int REQUEST_IMAGE_CAPTURE = 111;
    private DatabaseReference databaseReference;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private FusedLocationProviderClient mFusedLocationClient;
    Spinner s1,s2;
    EditText editText;
    List<Batiment> batList;
    Location mLocation;
    List<Salle> salleList;

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
        s1 = getActivity().findViewById(R.id.spinnerBatiment);
        s2 = getActivity().findViewById(R.id.spinnerSalle);
        s1.setOnItemSelectedListener(this);

        editText=getActivity().findViewById(R.id.textInputAutre);
        editText.setVisibility(View.GONE);
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
                                s1.setSelection(getPosition(getBestBat(mLocation)));
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
                setSalleList();


            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("firebase error :" + firebaseError.getDetails());
            }
        });
    }

    private void setSalleList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("salles");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                salleList = new ArrayList<Salle>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Salle salle= postSnapshot.getValue(Salle.class);
                    salleList.add(salle);
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
        return batList.size();
    }

    private String getBestBat(Location l) {
        double longitude = l.getLongitude();
        double latitude=l.getLatitude();
        double batLat1,batLat2,batLong1,batLong2;
        for(Batiment bat:batList){
            batLat1=bat.getLatitude1();batLat2=bat.getLatitude2();batLong1=bat.getLongitude1();batLong2=bat.getLongitude2();
            if((  ((batLat1<latitude)
                    && (latitude<batLat2))
                    || ((batLat1>latitude)
                    &&(latitude>batLat2)))
                    &&(  ((batLong1<longitude)
                    && (longitude<batLong2))
                    || ((batLong1>longitude)
                    && (longitude>batLong2)) )){
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
        if(sp1.contentEquals("Autre")) {
            s2.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
        }
        else {
            s2.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
            List<String> list = new ArrayList<String>();
            if(salleList!=null) {
                for (Salle salle : salleList) {
                    if (sp1.equals(salle.getBatiment())) {
                        list.add(salle.getNumero());
                    }
                }
            }
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