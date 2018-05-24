package ihm.si3.polytech.projetnote.visualisationincident;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.utility.Mishap;

public class DetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    static public final int REQUEST_LOCATION = 1;
    Mishap mishap;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mishap = (Mishap) getIntent().getSerializableExtra("Mishap");

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng localisation = new LatLng(mishap.getxPos(), mishap.getyPos());
        mMap.addMarker(new MarkerOptions().position(localisation).title(mishap.getTitle()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(localisation));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            startBeermay(); // <-- Start Beemray here
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBeermay(); // <-- Start Beemray here
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }

    private void startBeermay() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            // Show rationale and request permission.
        }

    }


}