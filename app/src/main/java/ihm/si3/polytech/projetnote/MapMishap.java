package ihm.si3.polytech.projetnote;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ihm.si3.polytech.projetnote.utility.Mishap;
import ihm.si3.polytech.projetnote.visualisationincident.NewsGridFragment;

import static ihm.si3.polytech.projetnote.visualisationincident.DetailsActivity.REQUEST_LOCATION;

public class MapMishap extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener {

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(4);


    //
// Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    private List<Mishap> mishapList;
    private MapView mapView;
    private GoogleMap googleMap;

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


        return inflater.inflate(R.layout.activity_mapline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment
        mishapList = NewsGridFragment.getInstance().getSelectedMishap();
        Toast.makeText(getContext(), "number of mishap " + mishapList.size(), Toast.LENGTH_LONG).show();



        // ViewCompat.setTransitionName(getActivity().findViewById(R.id.app_bar_layout));




    }

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
        createMarker();

        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions().clickable(true)

                .addAll(createLat())
                .color(Color.RED)
                .width(10));

        polyline1.setTag("B");
        stylePolyline(polyline1);
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4)); // TODO: 19/05/2018  je ne sais pas quelles cordonnées je dois prendre

        // Set listeners for click events.
        googleMap.setOnPolylineClickListener(this);
        // googleMap.setOnPolygonClickListener(this);

        googleMap.setBuildingsEnabled(true);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    private void createMarker() {

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(200, 50, conf);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setStrokeWidth(100);
        paint.setColor(Color.BLUE);
        canvas.drawText("TEXT", 0, 50, paint); // paint defines the text color, stroke width, size
        int i = 0;
        for (Mishap mishap : mishapList) {

            Marker p1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(mishap.getxPos(), mishap.getyPos()))
                    .title(mishap.getTitle()).snippet(mishap.getDescription()));
            p1.setTag(i);
            i++;
            mishap.setNumber(i);
            mishap.setMarker(p1);

        }
        RecyclerView recyclerView = getActivity().findViewById(R.id.post_rcycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdaptater = new MapCardRecycler(mishapList);
        recyclerView.setAdapter(mAdaptater);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    private List<LatLng> createLat() {
        List<LatLng> latLngs = new LinkedList<>();
        for (Mishap mishap : mishapList) {
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

    private void setupRecyclerView() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.post_rcycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdaptater);
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
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
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


}
