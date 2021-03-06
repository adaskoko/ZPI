package com.example.zpi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.databinding.ActivityUploadPhotoBinding;
import com.example.zpi.models.MultimediaFile;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.PhotoDao;
import com.google.firebase.storage.StorageReference;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadPhotoActivity extends AppCompatActivity {

    ActivityUploadPhotoBinding binding;
    Uri selectedMediaUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    final String TRIP_KEY = "TRIP";
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
        TextView tripNameTV = findViewById(R.id.tv_trip_name);
        tripNameTV.setText(trip.getName());
    }

    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            selectedMediaUri = data.getData();
            if (selectedMediaUri.toString().contains("image")) {
                binding.ivSelectedImage.setVisibility(View.VISIBLE);
                binding.vvVideo.setVisibility(View.INVISIBLE);
                binding.ivSelectedImage.setImageURI(selectedMediaUri);
            } else  if (selectedMediaUri.toString().contains("video")) {
                binding.ivSelectedImage.setVisibility(View.INVISIBLE);
                binding.vvVideo.setVisibility(View.VISIBLE);
                binding.vvVideo.setVideoURI(selectedMediaUri);
                binding.vvVideo.seekTo(1);
                binding.vvVideo.setMediaController(new MediaController(this){
                });
            }
        }
    }

    public void uploadImage(View view) {
        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Uploading File");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        String filename = formatter.format(now);

        storageReference = BaseConnection.getStorageInstance().getReference(trip.getID() + "/" + filename);

        storageReference.putFile(selectedMediaUri)
                .addOnSuccessListener(taskSnapshot -> {
                    binding.ivSelectedImage.setImageURI(null);
                    binding.vvVideo.setVideoURI(null);

                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                            task -> {
                                String url = task.getResult().toString();

                                MultimediaFile multimediaFile = new MultimediaFile(url, SharedPreferencesHandler.getLoggedInUser(getApplicationContext()), trip, binding.ivSelectedImage.getVisibility() == View.VISIBLE, new Date());

                                new Thread(() -> {
                                    try {
                                        new PhotoDao(BaseConnection.getConnectionSource()).create(multimediaFile);
                                        progressDialog.dismiss();
                                        finish();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                        progressDialog.dismiss();
                                    }
                                }).start();
                            });
                }).addOnFailureListener(e -> {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                    Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                });
    }

}