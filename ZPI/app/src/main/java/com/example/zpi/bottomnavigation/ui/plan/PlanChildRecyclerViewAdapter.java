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

import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PlanChildRecyclerViewAdapter extends RecyclerView.Adapter<PlanChildRecyclerViewAdapter.ChildViewHolder> {
    private List<TripPoint> tripPoints;
    private OnChildTripPointListener onChildTripPointListener;


    public PlanChildRecyclerViewAdapter(List<TripPoint> tripPoints, OnChildTripPointListener onChildTripPointListener) {
        this.tripPoints = tripPoints;
        this.onChildTripPointListener = onChildTripPointListener;

    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //View view = layoutInflater.inflate(R.layout.item_plan, parent, false);
        View view = layoutInflater.inflate(R.layout.item_plan, parent, false);
        return new ChildViewHolder(view, onChildTripPointListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        TripPoint point = tripPoints.get(position);

        holder.pointTitle.setText(point.getName());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        holder.pointTime.setText(dateFormat.format(point.getArrivalDate()));
        holder.pointAddress.setText(point.getTripPointLocation().getAddress());
        holder.pointId = point.getID();
    }

    @Override
    public int getItemCount() {
        Log.i("plan child size", String.valueOf(tripPoints.size()));

        return tripPoints.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView pointTitle;
        TextView pointTime;
        TextView pointAddress;
        int pointId;
        OnChildTripPointListener onChildTripPointListener;

        public ChildViewHolder(@NonNull View itemView, OnChildTripPointListener onChildTripPointListener) {
            super(itemView);
            pointTitle = itemView.findViewById(R.id.pointTitleTV);
            pointTime = itemView.findViewById(R.id.hourTV);
            pointAddress = itemView.findViewById(R.id.addressTV);

            this.onChildTripPointListener = onChildTripPointListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onChildTripPointListener.onChildClick(pointId);
        }
    }

    public interface OnChildTripPointListener {
        void onChildClick(int id);
    }
}
