package com.example.zpi.bottomnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.example.zpi.PhotoGalleryActivity;
import com.example.zpi.R;


import com.example.zpi.TripListActivity;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.UploadPhotoActivity;
import com.example.zpi.databinding.ActivityBottomNavigationBinding;
import com.example.zpi.models.Photo;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BottomNavigationActivity extends AppCompatActivity {

    private ActivityBottomNavigationBinding binding;
    public static final String TRIP_KEY = "TRIP";
    private Trip trip;
    private static final String TAG = "BottomNavigationActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Trip trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
        User user = SharedPreferencesHandler.getLoggedInUser(this);
        trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
//        Toast.makeText(this, trip.getName(), Toast.LENGTH_SHORT).show();

        binding = ActivityBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initTripTitle(trip);
        binding.btnBackToMainTripWindow.setOnClickListener(v -> {
            onBackPressed();
        });
        getLocationPermission();
        getDeviceLocation();



//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_todo, R.id.navigation_to_take_things, R.id.navigation_plan, R.id.mapsFragment, R.id.navigation_finance)
//                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation);
        NavController navController = navHostFragment.getNavController();
        binding.tvTripname.setOnClickListener(v -> {
            navController.navigateUp();
            navController.navigate(R.id.singleTripFragment);
        });
        //navView.getMenu().getItem(0).setVisible(false);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void initTripTitle(Trip trip) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        binding.tvTripname.setText(trip.getName());
        String startDate = dateFormat.format(trip.getStartDate());
        String endDate = dateFormat.format(trip.getEndDate());
        startDate += " - ";
        startDate += endDate;
        binding.tvTripdate.setText(startDate);
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            Date currentDate = new Date();
                            Log.d(TAG, "latitude " + currentLocation.getLatitude());
                            Log.d(TAG, "longitude " + currentLocation.getLongitude());

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(BottomNavigationActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    public void goToPhotos(View v){
        Intent intent = new Intent(this, PhotoGalleryActivity.class);
        intent.putExtra(TRIP_KEY, trip);
        startActivity(intent);
    }

}