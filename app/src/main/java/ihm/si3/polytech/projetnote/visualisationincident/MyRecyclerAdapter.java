package ihm.si3.polytech.projetnote.visualisationincident;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.login.StoreUsers;
import ihm.si3.polytech.projetnote.notused.DownloadImagesTask;
import ihm.si3.polytech.projetnote.utility.Mishap;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {


    private List<Mishap> mishapList;

    public MyRecyclerAdapter(List<Mishap> mishapList) {
        this.mishapList = mishapList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlayout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.cardView.setOnClickListener(new View.OnClickListener() // do something on click
        {

            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                //  intent.putExtra("mishap", mishapList.get(position));
                System.out.println("inside mamene");
                v.getContext().startActivity(intent);

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