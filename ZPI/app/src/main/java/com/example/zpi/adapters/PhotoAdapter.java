package com.example.zpi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.zpi.R;
import com.example.zpi.models.MultimediaFile;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    List<MultimediaFile> photos;
    private ClickListener clickListener;

    public PhotoAdapter(List<MultimediaFile> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_photo, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap photo = photos.get(position).getBitmap();
        ImageView photoIV = holder.imageIV;
        photoIV.setImageBitmap(photo);

        ImageView playIV = holder.playIV;
        if (photos.get(position).getPhoto()) {
            playIV.setVisibility(View.INVISIBLE);
        } else{
            playIV.setVisibility(View.VISIBLE);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageIV;
        public ImageView playIV;

        public ViewHolder(View itemView) {
            super(itemView);
            imageIV = (ImageView) itemView.findViewById(R.id.iv_image);
            playIV = (ImageView) itemView.findViewById(R.id.iv_play);

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
