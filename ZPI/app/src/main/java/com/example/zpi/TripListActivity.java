package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.adapters.TripAdapter;
import com.example.zpi.bottomnavigation.BottomNavigationActivity;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.models.UserLocation;
import com.example.zpi.repositories.TripDao;
import com.example.zpi.repositories.UserLocationDao;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TripListActivity extends AppCompatActivity {

    RecyclerView upcomingTripsRV;
    RecyclerView pastTripsRV;

    ArrayList<Trip> upcomingTrips;
    ArrayList<Trip> pastTrips;
    ArrayList<Trip> threeUpcomingTrips;
    ArrayList<Trip> threePastTrips;

    Trip currentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        upcomingTripsRV = (RecyclerView) findViewById(R.id.rv_upcoming_trips);
        pastTripsRV = (RecyclerView) findViewById(R.id.rv_past_trips);

        saveUserLocation();
        loadTrips();
    }

    private void saveUserLocation() {
        new Thread(() -> {
            User user = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
            Log.d(getClass().getSimpleName(), "user == null " + String.valueOf(user == null));
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            @SuppressLint("MissingPermission")
            final Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location lastKnownLocation = task.getResult();
                    new Thread(() -> {
                        try {
                            Log.d(getClass().getSimpleName(), "location = null " + String.valueOf(lastKnownLocation == null));
                            UserLocationDao userLocationDao = new UserLocationDao(BaseConnection.getConnectionSource());
                            Log.d(getClass().getSimpleName(), "location " + lastKnownLocation.getLatitude()+", "+lastKnownLocation.getLongitude());
                            //UserLocation userLocation = new UserLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), new Date(), user);
                            //userLocationDao.create(new UserLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), new Date(), user));
                            Log.d(getClass().getSimpleName(), "user location dao " + String.valueOf(userLocationDao == null));
                            UserLocation userLocation = userLocationDao.getUserLocationByUser(user);
                            Log.d(getClass().getSimpleName(), "user location " + String.valueOf(userLocation == null));

                            if (userLocation == null) {
                                userLocation = new UserLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), new Date(), user);
                                userLocationDao.create(userLocation);
                            } else {
                                userLocation.setLatitude(lastKnownLocation.getLatitude());
                                userLocation.setLongitude(lastKnownLocation.getLongitude());
                                userLocation.setTime(new Date());
                                userLocationDao.update(userLocation);
                            }
                            //userLocationDao.create(new UserLocation());
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }).start();

                } else {
                    Log.d(getClass().getSimpleName(), "Current location is null. Using defaults.");
                }
            });
        }).start();
    }

    private void loadTrips(){
        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Wczytywanie wycieczek...");
        progressDialog.show();
        new Thread(() -> {
            try {
                User user = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
                TextView currentUser = findViewById(R.id.userTV);
                assert user != null;
                String userName = user.getName();
                currentUser.setText(userName);
                TripDao tripDao = new TripDao(BaseConnection.getConnectionSource());
                List<List<Trip>> allTrips = tripDao.getPastAndFutureTripsForUser(user);
                upcomingTrips = (ArrayList<Trip>) allTrips.get(1);
                threeUpcomingTrips = (ArrayList<Trip>) upcomingTrips.stream().limit(3).collect(Collectors.toList());
                pastTrips = (ArrayList<Trip>) allTrips.get(0);
                threePastTrips = (ArrayList<Trip>) pastTrips.stream().limit(3).collect(Collectors.toList());
                currentTrip = tripDao.getCurrentTripForUser(user);

                setUpUpcomingRecyclerView();
                setUpPastRecyclerView();
                setUpCurrentTrip();
                tripDao.getConnectionSource().close();

                progressDialog.dismiss();
                //BaseConnection.closeConnection();
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
                progressDialog.dismiss();
            }

        }).start();
    }

    private void setUpCurrentTrip(){
        if (currentTrip != null){
            runOnUiThread(() -> {
                findViewById(R.id.ll_current_trip).setVisibility(LinearLayout.VISIBLE);
                TextView currentName = findViewById(R.id.tv_trip_name);
                currentName.setText(currentTrip.getName());
            });
        }
    }

    private void setUpUpcomingRecyclerView(){
        runOnUiThread(() -> {
            TripAdapter upcomingAdapter = new TripAdapter(threeUpcomingTrips);
            upcomingAdapter.setOnItemClickListener((position, v) -> goToTrip(upcomingTrips.get(position)));
            upcomingTripsRV.setAdapter(upcomingAdapter);
            LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            upcomingTripsRV.setLayoutManager(upcomingLayoutManager);
        });
    }

    private void setUpPastRecyclerView(){
        runOnUiThread(() -> {
            TripAdapter pastAdapter = new TripAdapter(threePastTrips);
            pastAdapter.setOnItemClickListener((position, v) -> goToTrip(pastTrips.get(position)));
            pastTripsRV.setAdapter(pastAdapter);
            LinearLayoutManager pastLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            pastTripsRV.setLayoutManager(pastLayoutManager);
        });
    }

    public void goToTrip(View view) {
        goToTrip(currentTrip);
    }

    public void goToTrip(Trip trip){
        Intent intent = new Intent(this, BottomNavigationActivity.class);
        intent.putExtra("TRIP", trip);
        startActivity(intent);
    }

    public void moreUpcoming(View view) {
        Intent intent = new Intent(this, TripGridActivity.class);
        intent.putExtra("TRIPS", upcomingTrips);
        intent.putExtra("TITLE", "Nadchodzące");
        startActivity(intent);
    }

    public void morePast(View view) {
        Intent intent = new Intent(this, TripGridActivity.class);
        intent.putExtra("TRIPS", pastTrips);
        intent.putExtra("TITLE", "Minione");
        startActivity(intent);
    }

    public void goToProfile(View view) {
        Intent intent = new Intent(this, UpdateUserActivity.class);
        startActivity(intent);
    }

    public void createNewTrip(View view) {
        Intent intent = new Intent(this, CreateTripActivity.class);
        startActivity(intent);
    }

    public void openChat(View v){
        Intent intent=new Intent(this, ChatListActivity.class);
        startActivity(intent);
    }
}