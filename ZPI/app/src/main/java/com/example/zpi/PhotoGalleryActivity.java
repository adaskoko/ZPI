package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.example.zpi.adapters.PhotoAdapter;
import com.example.zpi.adapters.TripAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Photo;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.PhotoDao;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryActivity extends AppCompatActivity {

    Trip trip;
    final String TRIP_KEY = "TRIP";
    ArrayList<Bitmap> bitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        Intent intent = getIntent();
        trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView galleryRV = findViewById(R.id.rv_gallery);

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
                    PhotoAdapter photoAdapter = new PhotoAdapter(bitmaps);
                    photoAdapter.setOnItemClickListener(new PhotoAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            showImage(position);
                        }
                    });
                    galleryRV.setAdapter(photoAdapter);
                    LinearLayoutManager upcomingLayoutManager = new GridLayoutManager(this, 3);
                    galleryRV.setLayoutManager(upcomingLayoutManager);
                });

                progressDialog.dismiss();

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
                progressDialog.dismiss();
            }
        }).start();
    }

    public void showImage(int position){
        Intent intent = new Intent(this, SinglePhotoActivity.class);
        intent.putExtra(TRIP_KEY, trip);
        intent.putExtra("POSITION", position);
        startActivity(intent);
    }

    public void addPhoto(View view) {
        Intent intent = new Intent(this, UploadPhotoActivity.class);
        intent.putExtra(TRIP_KEY, trip);
        startActivity(intent);
    }
}