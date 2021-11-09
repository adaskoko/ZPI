package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.zpi.adapters.ImageViewPagerAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Photo;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.PhotoDao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SinglePhotoActivity extends AppCompatActivity {


    final String TRIP_KEY = "TRIP";
    ArrayList<Bitmap> bitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo);

        Intent intent = getIntent();
        Trip trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
        int position = intent.getIntExtra("POSITION", 0);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Ładowanie zdjęć...");
        progressDialog.show();

        new Thread(() -> {
            try {
                List<Photo> photos = new PhotoDao(BaseConnection.getConnectionSource()).getPhotosFromTrip(trip);
                bitmaps = new ArrayList<>();
                for (Photo photo : photos){
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(photo.getUrl()).getContent());
                    bitmaps.add(bitmap);
                }

                runOnUiThread(() -> {
                    ViewPager viewPager = (ViewPager)findViewById(R.id.vp_images);
                    ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(SinglePhotoActivity.this, bitmaps);
                    viewPager.setAdapter(adapter);
                    viewPager.setCurrentItem(position);
                });

                progressDialog.dismiss();
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
                progressDialog.dismiss();
            }
        }).start();
    }
}