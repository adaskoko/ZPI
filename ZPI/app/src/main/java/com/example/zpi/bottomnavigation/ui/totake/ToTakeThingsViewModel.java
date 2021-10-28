package com.example.zpi.bottomnavigation.ui.totake;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.ProductToTakeDao;
import com.example.zpi.repositories.TripDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToTakeThingsViewModel extends ViewModel {

    private MutableLiveData<List<ProductToTake>> toTakeMLD;

    public LiveData<List<ProductToTake>> getProductToTakeList() {
        if (toTakeMLD == null) {
            toTakeMLD = new MutableLiveData<>(new ArrayList<>());
            try {
                loadProductToTake();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return toTakeMLD;
    }

    private void loadProductToTake() throws SQLException {
        new Thread(() -> {
            try {
                Trip trip = new TripDao(BaseConnection.getConnectionSource()).queryForEq("ID", 1).get(0);
                List<ProductToTake> toTake = new ProductToTakeDao(BaseConnection.getConnectionSource()).getProductsByTrip(trip);
                toTakeMLD = new MutableLiveData<>(toTake);
                Log.i("trips size", String.valueOf(toTake.size()));

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }).start();
    }
}