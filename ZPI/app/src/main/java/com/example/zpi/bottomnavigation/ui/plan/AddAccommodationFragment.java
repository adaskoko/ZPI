package com.example.zpi.bottomnavigation.ui.plan;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddAccomodationBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointType;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointTypeDao;

import java.sql.SQLException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;


public class AddAccommodationFragment extends Fragment {

    private FragmentAddAccomodationBinding binding;
    private Trip currTrip;

    public AddAccommodationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddAccomodationBinding.inflate(inflater, container, false);

        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");
        binding.btnAddAccPoint.setOnClickListener(c -> addAccommodation());
        return binding.getRoot();
    }

    private void addAccommodation() {
        String title = binding.nameAccET.getText().toString();
        String address = binding.adressOfAccET.getText().toString();
        String date_from = binding.dateOfAccET.getText().toString();
        String hour_from = binding.hhOfAccET.getText().toString();
        String minute_from = binding.mmOfAcctET.getText().toString();
        String date_to = binding.dateOfAccET2.getText().toString();
        String hour_to = binding.hhOfAccET2.getText().toString();
        String minute_to = binding.mmOfAcctET2.getText().toString();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String dateFrom = hour_from+":"+minute_from+" "+date_from;
        String dateTo = hour_to+":"+minute_to+" "+date_to;

        Date dDateFrom = new Date();
        Date dDateTo = new Date();
        try {
            dDateFrom = dateFormat.parse(dateFrom);
            dDateTo = dateFormat.parse(dateTo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TripPointLocation location = new TripPointLocation(0.0, 0.0, address);

        Date finalDDateFrom = dDateFrom;
        Date finalDDateTo = dDateTo;
        new Thread(() -> {
            try {
                TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
                TripPointType tripPointType = new TripPointTypeDao(BaseConnection.getConnectionSource()).getNoclegTripPointType();
                tripPointDao.createTripPoint(title, finalDDateFrom, finalDDateTo, null, currTrip, location, tripPointType);
                BaseConnection.closeConnection(Objects.requireNonNull(BaseConnection.getConnectionSource()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        NavHostFragment.findNavController(this).navigate(R.id.action_addAccomodation_to_navigation_plan);
    }
}