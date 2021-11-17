package com.example.zpi.bottomnavigation.ui.plan;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.R;
import com.example.zpi.models.TripPoint;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class AccommodationRecyclerViewAdapter extends RecyclerView.Adapter<AccommodationRecyclerViewAdapter.AccommodationViewHolder> {

    private List<TripPoint> tripPointList;
    private OnAccommodationListener onAccommodationListener;

    public AccommodationRecyclerViewAdapter(List<TripPoint> tripPointList, OnAccommodationListener onAccommodationListener) {
        this.tripPointList = tripPointList;
        this.onAccommodationListener = onAccommodationListener;
    }

    @NonNull
    @Override
    public AccommodationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accommodation, parent, false);
        return new AccommodationViewHolder(itemView, onAccommodationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AccommodationViewHolder holder, int position) {
        TripPoint point = tripPointList.get(position);
        holder.titleTV.setText(point.getName());
        holder.addressTV.setText(point.getTripPointLocation().getAddress());

        DateFormat dateFormat = new SimpleDateFormat("dd-MM");
        Date arrivalDate = point.getArrivalDate();
        Date departureDate = point.getDepartureDate();
        holder.dateTV.setText(dateFormat.format(point.getArrivalDate()) + " - " + dateFormat.format(point.getDepartureDate()));
    }

    @Override
    public int getItemCount() {
        return tripPointList.size();
    }

    class AccommodationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //TripPoint accommodation;
        TextView titleTV;
        TextView dateTV;
        TextView addressTV;
        OnAccommodationListener onAccommodationListener;


        public AccommodationViewHolder(@NonNull View itemView, OnAccommodationListener onAccommodationListener) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.accTitleTV);
            dateTV = itemView.findViewById(R.id.accDateTV);
            addressTV = itemView.findViewById(R.id.accAddressTV);
            this.onAccommodationListener=onAccommodationListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Log.i("recycler acc", "onClick");
            onAccommodationListener.onAccommodationClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnAccommodationListener {
        void onAccommodationClick(int position);
    }
}
