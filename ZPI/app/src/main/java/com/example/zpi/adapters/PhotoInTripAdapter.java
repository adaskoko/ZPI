package com.example.zpi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.zpi.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoInTripAdapter extends RecyclerView.Adapter<PhotoInTripAdapter.ViewHolder> {

    List<Bitmap> photos;
    private ClickListener clickListener;

    public PhotoInTripAdapter(List<Bitmap> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_photo_trip, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap photo = photos.get(position % photos.size());
        ImageView photoIV = holder.imageIV;
        photoIV.setImageBitmap(photo);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageIV;

        public ViewHolder(View itemView) {
            super(itemView);
            imageIV = (ImageView) itemView.findViewById(R.id.iv_image);

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
