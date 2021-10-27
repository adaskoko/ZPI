package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zpi.adapters.TripAdapter;
import com.example.zpi.bottomnavigation.BottomNavigationActivity;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripDao;

import java.sql.SQLException;
import java.util.List;

public class TripListActivity extends AppCompatActivity {

    RecyclerView upcomingTripsRV;
    RecyclerView pastTripsRV;

    List<Trip> upcomingTrips;
    List<Trip> pastTrips;

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

                upcomingTrips = tripDao.getFutureTripsForUser(user);
                pastTrips = tripDao.getPastTripsForUser(user);
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
            upcomingTripsRV.setAdapter(upcomingAdapter);
            LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            upcomingTripsRV.setLayoutManager(upcomingLayoutManager);
        });
    }

    private void setUpPastRecyclerView(){
        runOnUiThread(() -> {
            TripAdapter pastAdapter = new TripAdapter(pastTrips);
            pastTripsRV.setAdapter(pastAdapter);
            LinearLayoutManager pastLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            pastTripsRV.setLayoutManager(pastLayoutManager);
        });
    }

    public void goToTrip(View view) {
        Intent intent = new Intent(this, BottomNavigationActivity.class);
        intent.putExtra("TRIP", currentTrip);
        startActivity(intent);
    }
}