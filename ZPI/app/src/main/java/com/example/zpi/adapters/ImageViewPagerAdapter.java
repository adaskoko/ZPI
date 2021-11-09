package com.example.zpi.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.zpi.R;
import com.example.zpi.models.MultimediaFile;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImageViewPagerAdapter extends PagerAdapter {
    Context context;

    // Array of images
    List<MultimediaFile> multimediaFiles;

    // Layout Inflater
    LayoutInflater mLayoutInflater;


    // Viewpager Constructor
    public ImageViewPagerAdapter(Context context, List<MultimediaFile> multimediaFiles) {
        this.context = context;
        this.multimediaFiles = multimediaFiles;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return multimediaFiles.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_big_photo, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_image);
        VideoView videoView = (VideoView) itemView.findViewById(R.id.vv_video);

        MultimediaFile multimediaFile = multimediaFiles.get(position);
        if(multimediaFile.getPhoto()){
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);

            imageView.setImageBitmap(multimediaFile.getBitmap());
        }else{
            ProgressDialog progressDialog = new ProgressDialog(itemView.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            progressDialog.setTitle("Wczytywanie video...");
            progressDialog.show();

            imageView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);

            try {
                MediaController mediacontroller = new MediaController(imageView.getContext());
                mediacontroller.setAnchorView(videoView);
                Uri mVideo = Uri.parse(multimediaFile.getUrl());
                videoView.setMediaController(mediacontroller);
                videoView.setVideoURI(mVideo);

            } catch (Exception e) {
                e.printStackTrace();
            }

            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                    progressDialog.dismiss();
                }
            });
        }


        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}

