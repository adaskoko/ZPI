package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.zpi.adapters.ImageViewPagerAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.MultimediaFile;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.PhotoDao;
import com.example.zpi.repositories.UserDao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class SinglePhotoActivity extends AppCompatActivity {


    final String TRIP_KEY = "TRIP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo);

        Intent intent = getIntent();
        Trip trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
        int position = intent.getIntExtra("POSITION", 0);

        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Ładowanie zdjęć...");
        progressDialog.show();

        new Thread(() -> {
            try {
                List<MultimediaFile> multimediaFiles = new PhotoDao(BaseConnection.getConnectionSource()).getPhotosFromTrip(trip);
                UserDao userDao = new UserDao(BaseConnection.getConnectionSource());
                for (MultimediaFile multimediaFile : multimediaFiles){
                    userDao.refresh(multimediaFile.getUser());
                    if(multimediaFile.getPhoto()){
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(multimediaFile.getUrl()).getContent());
                        multimediaFile.setBitmap(bitmap);
                    }
                }

                runOnUiThread(() -> {
                    ViewPager viewPager = findViewById(R.id.vp_images);
                    ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(SinglePhotoActivity.this, multimediaFiles);
                    viewPager.setAdapter(adapter);
                    viewPager.setCurrentItem(position % multimediaFiles.size());
                });

                progressDialog.dismiss();
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
                progressDialog.dismiss();
            }
        }).start();
    }
}