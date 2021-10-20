package com.example.zpi.bottomnavigation.ui.todo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zpi.models.PreparationPoint;

import java.util.ArrayList;
import java.util.List;

public class ToDoViewModel extends ViewModel {

    private MutableLiveData<List<PreparationPoint>> preparationPointMLD;

    public LiveData<List<PreparationPoint>> getPreparationPointList() {
        if (preparationPointMLD == null) {
            preparationPointMLD = new MutableLiveData<>(new ArrayList<>());
            loadPreparationPoint();
        }
        return preparationPointMLD;
    }

    private void loadPreparationPoint() {
        // Do an asynchronous operation to fetch points.
    }
}