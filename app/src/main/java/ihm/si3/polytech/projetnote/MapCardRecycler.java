package ihm.si3.polytech.projetnote;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import ihm.si3.polytech.projetnote.utility.Mishap;

public class MapCardRecycler extends RecyclerView.Adapter<MapCardRecycler.MyViewHolder2> implements GoogleMap.OnMarkerClickListener {

    private Handler mHandler;
    private Runnable mAnimation;
    private GoogleMap googleMap;

    private List<Mishap> mishapList;


    public MapCardRecycler(List<Mishap> mishapList) {
        this.mishapList = mishapList;

        mHandler = new Handler();

    }


    @Override
    public MapCardRecycler.MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mapmishap_adaptateur, parent, false);
        return new MapCardRecycler.MyViewHolder2(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder2 holder, final int position) {

        holder.cardView.setOnClickListener(new View.OnClickListener() // do something on click
        {

            public void onClick(View v) {
                //
                Marker marker = mishapList.get(position).getMarker();

                final long start = SystemClock.uptimeMillis();
                final long duration = 1500L;

                // Cancels the previous animation
                mHandler.removeCallbacks(mAnimation);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mishapList.get(position).getxPos(), mishapList.get(position).getyPos()), 17));
                marker.showInfoWindow();

                // Starts the bounce animation
                mAnimation = new BounceAnimation(start, duration, marker, mHandler);
                mHandler.post(mAnimation);

            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mishapList.remove(position);
                return true;
            }
        });


        Mishap currentMishap = mishapList.get(position);
        holder.titleMishap.setText(currentMishap.getTitle());
        //    holder.description.setText(currentMishap.getDescription());
        holder.mainLetter.setText(String.valueOf(currentMishap.getNumber()));
        holder.batiment.setText(currentMishap.getLieu());


        //  DownloadImagesTask downloadImagesTask = new DownloadImagesTask(holder.imagePerson);
        //downloadImagesTask.execute(StoreUsers.getUrlPicture());

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // This causes the marker at Perth to bounce into position when it is clicked.
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500L;

        // Cancels the previous animation
        mHandler.removeCallbacks(mAnimation);

        // Starts the bounce animation
        mAnimation = new BounceAnimation(start, duration, marker, mHandler);
        mHandler.post(mAnimation);
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }


    @Override
    public int getItemCount() {
        return mishapList.size();
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

    public class MyViewHolder2 extends RecyclerView.ViewHolder {
        public TextView titleMishap;
        public TextView description;
        public CardView cardView;
        public TextView mainLetter;
        public TextView batiment;

        public MyViewHolder2(final View view) {
            super(view);
            titleMishap = view.findViewById(R.id.card_title);
            //description = view.findViewById(R.id.card_description);
            mainLetter = view.findViewById(R.id.main_letter);
            cardView = view.findViewById(R.id.cv);
            batiment = view.findViewById(R.id.batiment);


        }
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
