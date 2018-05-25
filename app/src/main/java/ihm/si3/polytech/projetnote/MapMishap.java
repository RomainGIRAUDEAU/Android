package ihm.si3.polytech.projetnote;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ihm.si3.polytech.projetnote.utility.Mishap;
import ihm.si3.polytech.projetnote.visualisationincident.NewsGridFragment;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;
import static ihm.si3.polytech.projetnote.visualisationincident.DetailsActivity.REQUEST_LOCATION;

public class MapMishap extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener {
    Location location;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(4);


    //
// Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    private List<Mishap> mishapList;
    private FirebaseFunctions mFunctions;
    private MapView mapView;
    private GoogleMap googleMap;
    List<Mishap> mishaps;

    MapCardRecycler mAdaptater;


    public static Fragment newInstance() {
        MapMishap fragment = new MapMishap();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        startBeermay1();


        mishaps = new ArrayList<>();
        mFunctions = FirebaseFunctions.getInstance();


        return inflater.inflate(R.layout.activity_mapline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment
        mishapList = NewsGridFragment.getInstance().getSelectedMishap();

        test()
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
                        // Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        // mMessageOutputField.setText(result);
                        // [END_EXCLUDE]
                    }
                });


        // ViewCompat.setTransitionName(getActivity().findViewById(R.id.app_bar_layout));


    }

    @Override
    public void onMapReady(GoogleMap map) {



        googleMap = map;
        createSchool();
        // createMarker();


    }

    private void createMarker() {


        int i = 1;
        for (Mishap mishap : mishaps) {

            Marker p1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(mishap.getxPos(), mishap.getyPos()))
                    .title(mishap.getTitle()).snippet(mishap.getDescription()));
            p1.setTag(i);

            mishap.setNumber(i);
            mishap.setMarker(p1);
            i++;

        }
        RecyclerView recyclerView = getActivity().findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mAdaptater = new MapCardRecycler(mishaps);
        mAdaptater.setGoogleMap(googleMap);
        recyclerView.setAdapter(mAdaptater);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions().clickable(true)

                .addAll(createLat())
                .color(Color.RED)
                .width(10));

        polyline1.setTag("B");
        stylePolyline(polyline1);


        // Set listeners for click events.
        googleMap.setOnPolylineClickListener(this);
        // googleMap.setOnPolygonClickListener(this);

        googleMap.setBuildingsEnabled(true);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            startBeermay(); // <-- Start Beemray here

        }


    }

    private List<LatLng> createLat() {
        List<LatLng> latLngs = new LinkedList<>();
        for (Mishap mishap : mishaps) {
            latLngs.add(new LatLng(mishap.getxPos(), mishap.getyPos()));
        }
        return latLngs;
    }

    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.fui_done_check_mark), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }


        polyline.setEndCap(new RoundCap());
        polyline.setWidth(10);
        polyline.setColor(Color.RED);
        polyline.setJointType(JointType.ROUND);
    }


    @Override
    public void onPolygonClick(Polygon polygon) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(getActivity(), "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            // Show rationale and request permission.
        }

    }

    private void startBeermay1() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));

        } else {
            // Show rationale and request permission.
        }

    }


    /**
     * Performs a bounce animation on a {@link Marker}.
     */
    private static class BounceAnimation implements Runnable {

        private final long mStart, mDuration;
        private final Interpolator mInterpolator;
        private final Marker mMarker;
        private final Handler mHandler;

        private BounceAnimation(long start, long duration, Marker marker, Handler handler) {
            mStart = start;
            mDuration = duration;
            mMarker = marker;
            mHandler = handler;
            mInterpolator = new BounceInterpolator();
        }

        @Override
        public void run() {
            long elapsed = SystemClock.uptimeMillis() - mStart;
            float t = Math.max(1 - mInterpolator.getInterpolation((float) elapsed / mDuration), 0f);
            mMarker.setAnchor(0.5f, 1.0f + 1.2f * t);

            if (t > 0.0) {
                // Post again 16ms later.
                mHandler.postDelayed(this, 16L);
            }
        }
    }

    private void createSchool() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.615436, 7.071840), 17));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(43.615811, 7.072427))
                .title("Batiment E")
                .snippet("4 niveaux")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_school_black_18dp)));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(43.615436, 7.071840))
                .title("Batiment O")
                .snippet("3 niveaux")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_school_black_18dp)));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(43.615059, 7.071306))
                .title("Batiment Forum")
                .snippet("3 niveaux")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_school_black_18dp)));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(43.614827, 7.071574))
                .title("Learning Center")
                .snippet("3 niveaux")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_book_black_18dp)));

    }

    private Task<String> test() {

        Map<String, Object> data = new HashMap<>();
        double x = location.getLatitude();
        double y = location.getLongitude();
        Map<String, Object> localisation = new HashMap<>();
        localisation.put("xPos", x);
        localisation.put("yPos", y);
        data.put("myLocalisation", localisation);
        Gson gson = new Gson();
        final Object json = gson.toJson(mishapList);

        data.put("mishap", json);

        return mFunctions
                .getHttpsCallable("findNearestPath")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) {


                        Gson gson = new GsonBuilder().create();
                        String wanted = new Gson().toJson(task.getResult().getData());

                        mishaps = gson.fromJson(wanted, new TypeToken<List<Mishap>>() {
                        }.getType());

                        createMarker();

                        return String.valueOf(mishaps.size());

                    }
                });

    }





}
