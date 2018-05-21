package ihm.si3.polytech.projetnote.creationincident;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by Tristan CORDIER on 21/05/2018.
 */

public class GPStracker implements LocationListener {
    private Context context;

    public GPStracker(Context context) {
        this.context = context;
    }

    public Location getLocation() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (isGPSEnabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(context,"Please enable permission",Toast.LENGTH_LONG).show();
                return null;
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,6000,10,this);
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            return location;
        }else {
            Toast.makeText(context,"Please enable gps",Toast.LENGTH_LONG).show();
        }
        return null;


    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
