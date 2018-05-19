package ihm.si3.polytech.projetnote;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
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
import ihm.si3.polytech.projetnote.visualisationincident.MyRecyclerAdapter;
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
    private MyRecyclerAdapter mAdapter;


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
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment
        mishapList = NewsGridFragment.getInstance().getSelectedMishap();
        Toast.makeText(getContext(), "number of mishap " + mishapList.size(), Toast.LENGTH_LONG).show();
        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyRecyclerAdapter mAdapter = new MyRecyclerAdapter(mishapList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));

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
        for (Mishap mishap : mishapList) {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(mishap.getxPos(), mishap.getyPos()))
                    .title(mishap.getTitle()).snippet(mishap.getDescription()));

        }
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
        RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerMap);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
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
}
