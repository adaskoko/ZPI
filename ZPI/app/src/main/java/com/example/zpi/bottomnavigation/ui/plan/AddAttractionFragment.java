package com.example.zpi.bottomnavigation.ui.plan;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.R;
import com.example.zpi.databinding.FragmentAddAttractionBinding;
import com.example.zpi.models.Trip;


public class AddAttractionFragment extends Fragment {

    private FragmentAddAttractionBinding binding;
    private Trip currTrip;

    public AddAttractionFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentAddAttractionBinding.inflate(inflater, container, false);
        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");
        binding.btnAddPrepPoint.setOnClickListener(c->addAttraction());
        return binding.getRoot();
    }

    private void addAttraction(){
        String title=binding.nameTripPointET.getText().toString();
        String address=binding.adressOfTripPointET.getText().toString();
        String date=binding.dateOfTripPointET.getText().toString();
        String hour=binding.hhOfTripPointET.getText().toString();
        String minute=binding.mmOfTripPointET.getText().toString();

        //convert address to location
        //create object
        //save to database

        NavHostFragment.findNavController(this).navigate(R.id.action_addAttraction_to_navigation_plan);
    }
}