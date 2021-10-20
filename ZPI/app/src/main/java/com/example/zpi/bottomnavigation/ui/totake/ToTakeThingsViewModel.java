package com.example.zpi.bottomnavigation.ui.totake;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.ProductToTake;

import java.util.List;

public class ToTakeThingsViewModel extends ViewModel {

    private MutableLiveData<List<ProductToTake>> toTakeMLD;

    public LiveData<List<ProductToTake>> getProductToTakeList() {
        if (toTakeMLD == null) {
            toTakeMLD = new MutableLiveData<>();
            loadProdouctToTake();
        }
        return toTakeMLD;
    }

    private void loadProdouctToTake() {
        // Do an asynchronous operation to fetch points.
    }
}