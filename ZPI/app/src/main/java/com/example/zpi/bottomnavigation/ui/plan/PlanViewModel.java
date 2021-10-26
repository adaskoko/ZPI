package com.example.zpi.bottomnavigation.ui.plan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zpi.models.TripPoint;

import java.util.ArrayList;
import java.util.List;

public class PlanViewModel extends ViewModel {

    private MutableLiveData<List<TripPoint>> tripPointMLD;

    public LiveData<List<TripPoint>> getTripPointList() {
        if (tripPointMLD == null) {
            tripPointMLD = new MutableLiveData<>(new ArrayList<>());
            loadTripPoint();
        }
        return tripPointMLD;
    }

    private void loadTripPoint() {
        // Do an asynchronous operation to fetch points.
    }
}