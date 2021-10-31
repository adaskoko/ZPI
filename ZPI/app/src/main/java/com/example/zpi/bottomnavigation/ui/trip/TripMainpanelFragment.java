package com.example.zpi.bottomnavigation.ui.trip;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.R;

public class TripMainpanelFragment extends Fragment {

    private TripMainpanelViewModel mViewModel;

    public static TripMainpanelFragment newInstance() {
        return new TripMainpanelFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.trip_mainpanel_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TripMainpanelViewModel.class);
        // TODO: Use the ViewModel
    }

}