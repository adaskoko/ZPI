package com.example.zpi.bottomnavigation.ui.finance;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zpi.R;
import com.example.zpi.bottomnavigation.ui.map.MapFragment;
import com.example.zpi.bottomnavigation.ui.map.MapViewModel;
import com.example.zpi.databinding.FragmentFinanceBinding;
import com.example.zpi.databinding.FragmentMapBinding;

public class FinanceFragment extends Fragment {

    private FinanceViewModel financeViewModel;
    private FragmentFinanceBinding binding;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);

        binding = FragmentFinanceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.financeTV;
        financeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                //textView.setText(s);
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