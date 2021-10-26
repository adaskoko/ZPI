package com.example.zpi.bottomnavigation.ui.totake;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.databinding.ItemToTakeBinding;


import java.util.ArrayList;
import java.util.List;

public class ToTakeThingRecyclerViewAdapter extends RecyclerView.Adapter<ToTakeThingRecyclerViewAdapter.ToTakeThingViewHolder> {

    private List<ProductToTake> productToTakeList;

    public ToTakeThingRecyclerViewAdapter(List<ProductToTake> items) {
        productToTakeList = items;
        //productToTakeList = new ArrayList<>();
    }

    @Override
    public ToTakeThingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ToTakeThingViewHolder(ItemToTakeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ToTakeThingViewHolder holder, int position) {
        ProductToTake product = productToTakeList.get(position);
        holder.mItem = product;
        holder.toTakeChB.setActivated(true); //jak w bazie jest zapisane chb
        holder.titleTV.setText(product.getName().toString());
        holder.personTV.setText(product.getUser().getName().toString());
    }

    public void setToTakeList(List<ProductToTake> list) {
        this.productToTakeList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productToTakeList.size();
    }

    public class ToTakeThingViewHolder extends RecyclerView.ViewHolder {
        public ProductToTake mItem;
        private final CheckBox toTakeChB;
        private final TextView personTV;
        private final TextView titleTV;

        public ToTakeThingViewHolder(ItemToTakeBinding binding) {
            super(binding.getRoot());
            toTakeChB = binding.toTakeChB;
            personTV = binding.personTV;
            titleTV = binding.titleTV;
        }
    }
}