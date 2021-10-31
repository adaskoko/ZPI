package com.example.zpi.bottomnavigation.ui.plan;

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

import com.example.zpi.databinding.FragmentPlanBinding;
import com.example.zpi.models.TripPoint;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class PlanFragment extends Fragment {

    private PlanViewModel planViewModel;
    private FragmentPlanBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        planViewModel = new ViewModelProvider(this).get(PlanViewModel.class);
        binding = FragmentPlanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView planRV = binding.list;
        planRV.setLayoutManager(new LinearLayoutManager(getContext()));
        planRV.setHasFixedSize(true);

        PlanRecyclerViewAdapter planRecyclerViewAdapter = new PlanRecyclerViewAdapter(planViewModel.getTripPointList().getValue());
        planRV.setAdapter(planRecyclerViewAdapter);

        planViewModel.getTripPointList().observe(getViewLifecycleOwner(), new Observer<List<TripPoint>>() {
            @Override
            public void onChanged(List<TripPoint> tripPoints) {
                // update list
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