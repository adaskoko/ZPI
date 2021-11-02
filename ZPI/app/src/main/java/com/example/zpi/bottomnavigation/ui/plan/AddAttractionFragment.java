package com.example.zpi.bottomnavigation.ui.plan;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddAttractionBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.repositories.TripPointDao;

import java.sql.SQLException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;


public class AddAttractionFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private FragmentAddAttractionBinding binding;
    private Trip currTrip;

    public AddAttractionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = requireActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra(TRIP_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddAttractionBinding.inflate(inflater, container, false);

        binding.btnAddPrepPoint.setOnClickListener(c -> addAttraction());
        return binding.getRoot();
    }

    private void addAttraction() {
        String title = binding.nameTripPointET.getText().toString();
        String address = binding.adressOfTripPointET.getText().toString();
        String sDate = binding.dateOfTripPointET.getText().toString();
        String hour = binding.hhOfTripPointET.getText().toString();
        String minute = binding.mmOfTripPointET.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        Date date1;
        try {
            String date = hour+":"+minute+" "+sDate;
            date1 = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        TripPointLocation tripPointLocation = new TripPointLocation();
        tripPointLocation.setLatitude(0.0);
        tripPointLocation.setLongitude(0.0);
        tripPointLocation.setAddress(address);


        //convert address to location
        //create object
        //save to database

        NavHostFragment.findNavController(this).navigate(R.id.action_addAttraction_to_navigation_plan);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}