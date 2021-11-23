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

import java.util.List;


public class PlanRecyclerViewAdapter extends RecyclerView.Adapter<PlanRecyclerViewAdapter.PlanViewHolder> {

    private List<Section> sectionList;
    private PlanChildRecyclerViewAdapter.OnChildTripPointListener onChildTripPointListener;


    public PlanRecyclerViewAdapter(List<Section> sections, PlanChildRecyclerViewAdapter.OnChildTripPointListener onChildTripPointListener) {
        this.sectionList = sections;
        this.onChildTripPointListener = onChildTripPointListener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_plan_one_day, parent,false);
        //View view = layoutInflater.inflate(R.layout.plan_child_item, parent,false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        //Log.i("plan position", String.valueOf(position));
        Section section = sectionList.get(position);
        String sectionTitle = section.getTitle();
        sectionTitle = sectionTitle.substring(0, 5);
        List<TripPoint> items = section.getPointList();


        holder.sectionTitle.setText(sectionTitle);

        PlanChildRecyclerViewAdapter childRecyclerViewAdapter = new PlanChildRecyclerViewAdapter(items, onChildTripPointListener);
        holder.childList.setAdapter(childRecyclerViewAdapter);
        int height=holder.childList.getMeasuredHeight();
        holder.line.setMinimumHeight(height);
    }

    @Override
    public int getItemCount() {
        Log.i("plan section", String.valueOf(sectionList.size()));
        return sectionList.size();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionTitle;
        private RecyclerView childList;
        private View line;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.dayDateTV);
            childList = itemView.findViewById(R.id.list);
            line=itemView.findViewById(R.id.line);
        }
    }
}