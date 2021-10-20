package com.example.zpi.bottomnavigation.ui.plan;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.databinding.ItemPlanBinding;
import com.example.zpi.models.TripPoint;

import java.util.List;

public class PlanRecyclerViewAdapter extends RecyclerView.Adapter<PlanRecyclerViewAdapter.PlanViewHolder> {

    private final List<TripPoint> tripPointList;

    public PlanRecyclerViewAdapter(List<TripPoint> items) {
        tripPointList = items;
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlanViewHolder(ItemPlanBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final PlanViewHolder holder, int position) {
        holder.tripPoint = tripPointList.get(position);
        holder.mIdView.setText(String.valueOf(tripPointList.get(position).getID()));
        holder.mContentView.setText(tripPointList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return tripPointList.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public TripPoint tripPoint;

        public PlanViewHolder(ItemPlanBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}