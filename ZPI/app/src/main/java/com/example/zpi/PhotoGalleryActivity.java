package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.example.zpi.adapters.PhotoAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.MultimediaFile;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.PhotoDao;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
        TextView tripNameTV = findViewById(R.id.tv_trip_name);
        tripNameTV.setText(trip.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView galleryRV = findViewById(R.id.rv_gallery);

        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Ładowanie zdjęć...");
        progressDialog.show();

        new Thread(() -> {
            try {
                List<MultimediaFile> multimediaFiles = new PhotoDao(BaseConnection.getConnectionSource()).getPhotosFromTrip(trip);
                bitmaps = new ArrayList<>();
                for (MultimediaFile multimediaFile : multimediaFiles){
                    if(multimediaFile.getPhoto()){
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(multimediaFile.getUrl()).getContent());
                        multimediaFile.setBitmap(bitmap);
                    }else{
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(multimediaFile.getUrl(), new HashMap<String, String>());
                        Bitmap thumb = mediaMetadataRetriever.getFrameAtTime();
                        multimediaFile.setBitmap(thumb);
                    }
                }

                runOnUiThread(() -> {
                    PhotoAdapter photoAdapter = new PhotoAdapter(multimediaFiles);
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

    public void back(View view) {
        finish();
    }
}