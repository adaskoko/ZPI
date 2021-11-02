package com.example.zpi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.util.List;

public class TripMainActivity extends AppCompatActivity {

    public User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
    }

    public void gotoUserDetails(View v){
        Intent intent=new Intent(this, UpdateUserActivity.class);
        startActivity(intent);
    }

    public void createNewTrip(View v){
        Intent intent=new Intent(this, CreateTripActivity.class);
        startActivity(intent);
    }

    public void gotoFutureTrips(View v){
        //not yet implemented
    }

    public void gotoPastTrips(View v){
        //not yet implemented
    }

    public void gotoChat(View v){
        //not yet implemented
    }

    private void loadFutureTripsPreview(){
        new Thread(() -> {
            try {
                TripDao tripDao = new TripDao(BaseConnection.getConnectionSource());
                List<Trip> usersPastTrips=tripDao.getFutureTripsForUser(loggedUser);
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    private void loadPastTripsPreview(){
        new Thread(() -> {
            try {
                TripDao tripDao = new TripDao(BaseConnection.getConnectionSource());
                List<Trip> usersPastTrips=tripDao.getPastTripsForUser(loggedUser);
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }
}