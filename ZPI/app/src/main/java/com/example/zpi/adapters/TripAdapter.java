package com.example.zpi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.zpi.R;
import com.example.zpi.models.Trip;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    List<Trip> trips;
    private ClickListener clickListener;

    public TripAdapter(List<Trip> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_trip, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = trips.get(position);

        TextView tripNameTV = holder.tripNameTV;
        tripNameTV.setText(trip.getName());
        TextView tripDatesTV = holder.tripDatesTV;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
        tripDatesTV.setText(simpleDateFormat.format(trip.getStartDate()) + " - " + simpleDateFormat.format(trip.getEndDate()));
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tripNameTV;
        public TextView tripDatesTV;

        public ViewHolder(View itemView) {
            super(itemView);
            tripNameTV = (TextView) itemView.findViewById(R.id.tv_trip_name);
            tripDatesTV = (TextView) itemView.findViewById(R.id.tv_trip_dates);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }


}
