package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zpi.adapters.TripAdapter;
import com.example.zpi.bottomnavigation.BottomNavigationActivity;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TripListActivity extends AppCompatActivity {

    RecyclerView upcomingTripsRV;
    RecyclerView pastTripsRV;

    ArrayList<Trip> upcomingTrips;
    ArrayList<Trip> pastTrips;

    Trip currentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        upcomingTripsRV = (RecyclerView) findViewById(R.id.rv_upcoming_trips);
        pastTripsRV = (RecyclerView) findViewById(R.id.rv_past_trips);

        loadTrips();
    }

    private void loadTrips(){
        new Thread(() -> {
            try {
                User user = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
                TripDao tripDao = new TripDao(BaseConnection.getConnectionSource());

                upcomingTrips = (ArrayList<Trip>) tripDao.getFutureTripsForUser(user);
                pastTrips = (ArrayList<Trip>) tripDao.getPastTripsForUser(user);
                currentTrip = tripDao.getCurrentTripForUser(user);

                setUpUpcomingRecyclerView();
                setUpPastRecyclerView();
                setUpCurrentTrip();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
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
            TripAdapter upcomingAdapter = new TripAdapter(upcomingTrips);
            upcomingAdapter.setOnItemClickListener(new TripAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    goToTrip(upcomingTrips.get(position));
                }
            });
            upcomingTripsRV.setAdapter(upcomingAdapter);
            LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            upcomingTripsRV.setLayoutManager(upcomingLayoutManager);
        });
    }

    private void setUpPastRecyclerView(){
        runOnUiThread(() -> {
            TripAdapter pastAdapter = new TripAdapter(pastTrips);
            pastAdapter.setOnItemClickListener(new TripAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    goToTrip(pastTrips.get(position));
                }
            });
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
}