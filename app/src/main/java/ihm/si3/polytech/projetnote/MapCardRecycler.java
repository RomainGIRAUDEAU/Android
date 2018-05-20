package ihm.si3.polytech.projetnote;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ihm.si3.polytech.projetnote.login.StoreUsers;
import ihm.si3.polytech.projetnote.notused.DownloadImagesTask;
import ihm.si3.polytech.projetnote.utility.Mishap;

public class MapCardRecycler extends RecyclerView.Adapter<MapCardRecycler.MyViewHolder> {

    private List<Mishap> mishapList;

    public MapCardRecycler(List<Mishap> mishapList) {
        this.mishapList = mishapList;

    }

    @Override
    public MapCardRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlayout, parent, false);
        return new MapCardRecycler.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.cardView.setOnClickListener(new View.OnClickListener() // do something on click
        {

            public void onClick(View v) {
                // holder.cardView.setBackgroundColor(Color.RED);


            }
        });


        Mishap currentMishap = mishapList.get(position);
        holder.titleMishap.setText(currentMishap.getTitle());
        holder.description.setText(currentMishap.getDescription());
        holder.username.setText(currentMishap.getAuthor());


        DownloadImagesTask downloadImagesTask = new DownloadImagesTask(holder.imagePerson);
        downloadImagesTask.execute(StoreUsers.getUrlPicture());

    }


    @Override
    public int getItemCount() {
        return mishapList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleMishap;
        public TextView description;
        public TextView username;
        public ImageView imagePerson;
        public CardView cardView;

        public MyViewHolder(final View view) {
            super(view);
            titleMishap = view.findViewById(R.id.card_title);
            description = view.findViewById(R.id.card_description);
            username = view.findViewById(R.id.person_username);
            imagePerson = view.findViewById(R.id.person_picture);
            cardView = view.findViewById(R.id.cv);

        }
    }


}
