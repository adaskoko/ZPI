package com.example.zpi.bottomnavigation.ui.totake;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.databinding.FragmentToTakeThingsBinding;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.ProductToTakeDao;
import com.example.zpi.repositories.TripDao;

import java.sql.SQLException;
import java.util.List;


public class ToTakeThingsFragment extends Fragment {

    private ToTakeThingsViewModel toTakeThingsViewModel;
    private FragmentToTakeThingsBinding binding;
    private ToTakeThingRecyclerViewAdapter toTakeThingRecyclerViewAdapter;
    private Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        toTakeThingsViewModel = new ViewModelProvider(this).get(ToTakeThingsViewModel.class);

        binding = FragmentToTakeThingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView toTakeThingsRV = binding.recyclerViewToTakeThings;
        toTakeThingsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        toTakeThingsRV.setHasFixedSize(true);

//        new Thread(() -> {
//            getActivity().runOnUiThread(() -> {
//                toTakeThingRecyclerViewAdapter = new ToTakeThingRecyclerViewAdapter(toTakeThingsViewModel.getProductToTakeList().getValue());
//                toTakeThingsRV.setAdapter(toTakeThingRecyclerViewAdapter);
//            });
//
//        }).start();

        new Thread(() -> {
            try {
                Trip trip = new TripDao(BaseConnection.getConnectionSource()).queryForEq("ID", 1).get(0);
                List<ProductToTake> products = new ProductToTakeDao(BaseConnection.getConnectionSource()).getProductsByTrip(trip);

                Log.i("to take size fragment", String.valueOf(products.size()));
                getActivity().runOnUiThread(() -> {
                    toTakeThingRecyclerViewAdapter = new ToTakeThingRecyclerViewAdapter(products);
                    toTakeThingsRV.setAdapter(toTakeThingRecyclerViewAdapter);
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }).start();

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}