package com.example.zpi.bottomnavigation.ui.plan;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.R;
import com.example.zpi.databinding.FragmentAddAccomodationBinding;
import com.example.zpi.models.Trip;


public class AddAccomodationFragment extends Fragment {

    private FragmentAddAccomodationBinding binding;
    private Trip currTrip;

    public AddAccomodationFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentAddAccomodationBinding.inflate(inflater, container, false);

        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");
        binding.btnAddAccPoint.setOnClickListener(c->addAccomodation());
        return binding.getRoot();
    }

    private void addAccomodation(){
        String title=binding.nameAccET.getText().toString();
        String address=binding.adressOfAccET.getText().toString();
        String date_1=binding.dateOfAccET.getText().toString();
        String hour_1=binding.hhOfAccET.getText().toString();
        String minute_1=binding.mmOfAcctET.getText().toString();
        String date_2=binding.dateOfAccET2.getText().toString();
        String hour_2=binding.hhOfAccET2.getText().toString();
        String minute_2=binding.mmOfAcctET2.getText().toString();

        //parse, create and save

    }
}