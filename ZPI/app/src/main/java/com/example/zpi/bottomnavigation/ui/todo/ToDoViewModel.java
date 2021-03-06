package com.example.zpi.bottomnavigation.ui.todo;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.TripDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToDoViewModel extends ViewModel {

    private MutableLiveData<List<PreparationPoint>> preparationPointMLD;

    public LiveData<List<PreparationPoint>> getPreparationPointList() {
        if (preparationPointMLD == null) {
            preparationPointMLD = new MutableLiveData<>(new ArrayList<>());
            try {
                loadPreparationPoint();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return preparationPointMLD;
    }

    private void loadPreparationPoint() throws SQLException {
        new Thread(() -> {
            try {
                Trip trip = new TripDao(BaseConnection.getConnectionSource()).queryForEq("ID", 1).get(0);
                List<PreparationPoint> todos = new PreparationPointDao(BaseConnection.getConnectionSource()).getPreparationPointsByTrip(trip);
                preparationPointMLD = new MutableLiveData<>(todos);
                Log.i("trips size", String.valueOf(todos.size()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }).start();
    }
}