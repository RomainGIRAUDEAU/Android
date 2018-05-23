package ihm.si3.polytech.projetnote.visualisationincident;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.battleent.ribbonviews.RibbonLayout;

import java.util.ArrayList;
import java.util.List;

import ihm.si3.polytech.projetnote.R;
import ihm.si3.polytech.projetnote.notused.DownloadImagesTask;
import ihm.si3.polytech.projetnote.utility.Mishap;
import ihm.si3.polytech.projetnote.utility.Priority;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {


    private List<Mishap> mishapList;


    private List<Mishap> selectedMishap;

    public MyRecyclerAdapter(List<Mishap> mishapList) {
        this.mishapList = mishapList;
        selectedMishap = new ArrayList<>();
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
                intent.putExtra("Mishap", mishapList.get(position));
                v.getContext().startActivity(intent);


            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!mishapList.get(position).isSelectedItem()) {
                    mishapList.get(position).setSelectedItem(true);
                    holder.cardView.setBackgroundColor(Color.RED);
                    selectedMishap.add(mishapList.get(position));

                } else {
                    mishapList.get(position).setSelectedItem(false);
                    holder.cardView.setBackgroundColor(Color.WHITE);
                    selectedMishap.remove(mishapList.get(position));
                }

                // holder.cardView.setBackgroundColor(Color.BLUE);
                return true;
            }
        });


        Mishap currentMishap = mishapList.get(position);
        holder.titleMishap.setText(currentMishap.getTitle());
        holder.description.setText(currentMishap.getDescription());
        holder.username.setText(currentMishap.getAuthor());
        String priority = currentMishap.getPriority().name();
        if (currentMishap.getDate() != null) {
            holder.ribbonLayout.setBottomText(currentMishap.getDate());
        }

        if (currentMishap.getPriority() == Priority.CRITICAL) {
            holder.ribbonLayout.setHeaderRibbonColor(Color.RED);
        } else if (currentMishap.getPriority() == Priority.HIGH) {
            holder.ribbonLayout.setHeaderRibbonColor(Color.YELLOW);
        } else if (currentMishap.getPriority() == Priority.MEDIUM) {
            holder.ribbonLayout.setHeaderRibbonColor(Color.CYAN);
        } else if (currentMishap.getPriority() == Priority.LOW) {
            holder.ribbonLayout.setHeaderRibbonColor(Color.GREEN);
        }
        holder.ribbonLayout.setHeaderText(priority);


        DownloadImagesTask downloadImagesTask = new DownloadImagesTask(holder.imagePerson);
        downloadImagesTask.execute(currentMishap.getUrlPicture());

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
        public RibbonLayout ribbonLayout;

        public MyViewHolder(final View view) {
            super(view);
            titleMishap = view.findViewById(R.id.card_title);
            description = view.findViewById(R.id.card_description);
            username = view.findViewById(R.id.person_username);
            imagePerson = view.findViewById(R.id.person_picture);
            cardView = view.findViewById(R.id.cv);
            ribbonLayout = view.findViewById(R.id.ribbonTag);

        }
    }

    public List<Mishap> getSelectedMishap() {
        return selectedMishap;
    }
}