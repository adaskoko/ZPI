package com.example.zpi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.zpi.R;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImageViewPagerAdapter extends PagerAdapter {
    Context context;

    // Array of images
    List<Bitmap> images;

    // Layout Inflater
    LayoutInflater mLayoutInflater;


    // Viewpager Constructor
    public ImageViewPagerAdapter(Context context, List<Bitmap> images) {
        this.context = context;
        this.images = images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item_big_photo, container, false);

        // referencing the image view from the item.xml file
        ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_image);

        // setting the image in the imageView
        imageView.setImageBitmap(images.get(position));

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}

