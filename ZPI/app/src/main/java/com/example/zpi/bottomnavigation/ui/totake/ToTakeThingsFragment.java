package com.example.zpi.bottomnavigation.ui.totake;

import android.os.Bundle;
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

import com.example.zpi.databinding.FragmentToTakeThingsBinding;
import com.example.zpi.models.ProductToTake;

import java.util.List;


public class ToTakeThingsFragment extends Fragment {

    private ToTakeThingsViewModel toTakeThingsViewModel;
    private FragmentToTakeThingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        toTakeThingsViewModel = new ViewModelProvider(this).get(ToTakeThingsViewModel.class);

        binding = FragmentToTakeThingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView toTakeThingsRV = binding.recyclerViewToTakeThings;
        toTakeThingsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        toTakeThingsRV.setHasFixedSize(true);

        ToTakeThingRecyclerViewAdapter toTakeThingRecyclerViewAdapter = new ToTakeThingRecyclerViewAdapter(toTakeThingsViewModel.getProductToTakeList().getValue());
        toTakeThingsRV.setAdapter(toTakeThingRecyclerViewAdapter);

        toTakeThingsViewModel.getProductToTakeList().observe(getViewLifecycleOwner(), new Observer<List<ProductToTake>>() {
            @Override
            public void onChanged(List<ProductToTake> productToTakes) {
                //update list
                Toast.makeText(getContext(), "Updated list", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}