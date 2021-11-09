package com.example.zpi;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.databinding.ActivityUploadPhotoBinding;
import com.example.zpi.models.Photo;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.PhotoDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.j256.ormlite.stmt.query.In;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadPhotoActivity extends AppCompatActivity {

    ActivityUploadPhotoBinding binding;
    Uri imageUri;
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
    }

    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/* video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            binding.ivSelectedImage.setImageURI(imageUri);
        }
    }

    public void uploadImage(View view) {
        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Uploading File");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        String filename = formatter.format(now);

        storageReference = BaseConnection.getStorageInstance().getReference("images/" + filename);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        binding.ivSelectedImage.setImageURI(null);

                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String url = task.getResult().toString();

                                        Photo photo = new Photo(url, SharedPreferencesHandler.getLoggedInUser(getApplicationContext()), trip);
                                        new Thread(() -> {
                                            try {
                                                new PhotoDao(BaseConnection.getConnectionSource()).create(photo);
                                                progressDialog.dismiss();
                                            } catch (SQLException throwables) {
                                                throwables.printStackTrace();
                                                progressDialog.dismiss();
                                            }
                                        }).start();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}