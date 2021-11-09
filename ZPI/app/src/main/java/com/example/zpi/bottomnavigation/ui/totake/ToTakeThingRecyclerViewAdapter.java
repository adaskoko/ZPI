package com.example.zpi.bottomnavigation.ui.totake;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.databinding.ItemToTakeBinding;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.ProductToTakeDao;


import java.sql.SQLException;
import java.util.List;

public class ToTakeThingRecyclerViewAdapter extends RecyclerView.Adapter<ToTakeThingRecyclerViewAdapter.ToTakeThingViewHolder> {

    private List<ProductToTake> productToTakeList;
    private final ToTakeThingListener toTakeThingListener;

    public ToTakeThingRecyclerViewAdapter(List<ProductToTake> items, ToTakeThingListener toTakeThingListener) {
        productToTakeList = items;
        this.toTakeThingListener = toTakeThingListener;
        //productToTakeList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ToTakeThingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_take, parent, false);
        //return new ToTakeThingViewHolder(ItemToTakeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), toTakeThingListener);
        return new ToTakeThingViewHolder(itemView, toTakeThingListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ToTakeThingViewHolder holder, int position) {
        ProductToTake product = productToTakeList.get(position);
        holder.mItem = product;
        //holder.toTakeChB.setActivated(product.isDone()); //jak w bazie jest zapisane chb
        holder.titleTV.setText(product.getName());
        holder.personTV.setText(product.getUser().getName());
    }

    @Override
    public int getItemCount() {

        return productToTakeList.size();
    }

    public void  deleteToTakeThingPosition(int position) {
        ProductToTake product = productToTakeList.get(position);

        new Thread(() -> {
            try {
                ProductToTakeDao productDao = new ProductToTakeDao(BaseConnection.getConnectionSource());
                productDao.delete(product);
                Log.i("toTake", "toTake delete");
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        productToTakeList.remove(position);
        //notifyDataSetChanged();
        notifyItemRemoved(position);
    }
    public ProductToTake getProduct(int position){

        return productToTakeList.get(position);
    }


    class ToTakeThingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ProductToTake mItem;
        private final CheckBox toTakeChB;
        private final TextView personTV;
        private final TextView titleTV;
        private final ToTakeThingListener toTakeThingListener;

        public ToTakeThingViewHolder(@NonNull View itemView, ToTakeThingListener toTakeThingListener) {
//            super(itemView);
//            toTakeChB = itemView.findViewById(R.id.toTakeChB);
//            personTV = itemView.findViewById(R.id.personTV);
//            titleTV = itemView.findViewById(R.id.titleTV);
            super(itemView);
            toTakeChB = itemView.findViewById(R.id.toTakeChB);
            personTV = itemView.findViewById(R.id.personTV);
            titleTV = itemView.findViewById(R.id.titleTV);

            this.toTakeThingListener = toTakeThingListener;
            itemView.setOnClickListener(this);
            toTakeChB.setOnClickListener(v -> mItem.setDone(!mItem.isDone()));
        }

        @Override
        public void onClick(View v) {
            toTakeThingListener.toTakeThingClick(getAbsoluteAdapterPosition());
        }


    }

    public interface ToTakeThingListener {
        void toTakeThingClick(int position);
    }
}