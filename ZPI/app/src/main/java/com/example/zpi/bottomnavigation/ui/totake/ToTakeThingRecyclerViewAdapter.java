package com.example.zpi.bottomnavigation.ui.totake;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zpi.models.ProductToTake;
import com.example.zpi.databinding.ItemToTakeBinding;


import java.util.List;

public class ToTakeThingRecyclerViewAdapter extends RecyclerView.Adapter<ToTakeThingRecyclerViewAdapter.ToTakeThingViewHolder> {

    private final List<ProductToTake> productToTakeList;

    public ToTakeThingRecyclerViewAdapter(List<ProductToTake> items) {
        productToTakeList = items;
    }

    @Override
    public ToTakeThingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ToTakeThingViewHolder(ItemToTakeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ToTakeThingViewHolder holder, int position) {
        holder.mItem = productToTakeList.get(position);
        holder.mIdView.setText(String.valueOf(productToTakeList.get(position).getID()));
        holder.mContentView.setText(productToTakeList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return productToTakeList.size();
    }

    public class ToTakeThingViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public ProductToTake mItem;

        public ToTakeThingViewHolder(ItemToTakeBinding binding) {
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