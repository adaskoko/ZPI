package com.example.zpi.bottomnavigation.ui.plan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.zpi.R;
import com.example.zpi.bottomnavigation.BottomNavigationActivity;
import com.example.zpi.bottomnavigation.ui.todo.TodoRecyclerViewAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAttractionDetailsBinding;
import com.example.zpi.databinding.FragmentAttractionEditBinding;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.TripParticipantDao;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointParticipantDao;

import java.sql.SQLException;
import java.sql.Struct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class AttractionEditFragment extends Fragment implements DatePickerDialog.OnDateSetListener, ParticipantsRecyclerViewAdapter.OnParticipantListener {

    private FragmentAttractionEditBinding binding;
    private Trip currTrip;
    private TripPoint currPoint;
    private ParticipantsRecyclerViewAdapter participantsRecyclerViewAdapter;
    private List<TripPointParticipant> participants;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currPoint = (TripPoint) getArguments().get(PlanFragment.PLAN_KEY);
        }
        Intent intent = requireActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra(BottomNavigationActivity.TRIP_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAttractionEditBinding.inflate(inflater, container, false);
        fillEditTexts();
        binding.etDateOfTripPoint.setOnClickListener(c -> showDatePickerDialog());
        binding.btnAddPrepPoint.setOnClickListener(c -> save());
        return binding.getRoot();
    }

    private void save() {
        String title = binding.etNameTripPoint.getText().toString();
        String address = binding.etAdressOfTripPoint.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String date = binding.etHhOfTripPoint.getText().toString()+":"+binding.etMmOfTripPoint.getText().toString()+" "+ binding.etDateOfTripPoint.getText().toString();
        currPoint.setName(title);
        try {
            currPoint.setArrivalDate(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TripPointLocation tripPointLocation = currPoint.getTripPointLocation();
        tripPointLocation.setAddress(address);
        currPoint.setTripPointLocation(tripPointLocation);
        new Thread(() -> {
            try {
                TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
                tripPointDao.update(currPoint);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

    }

    private void fillEditTexts() {
        binding.etNameTripPoint.setText(currPoint.getName());
        binding.etAdressOfTripPoint.setText(currPoint.getTripPointLocation().getAddress());
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(currPoint.getArrivalDate());
        binding.etDateOfTripPoint.setText(date);

        DateFormat hourFormat = new SimpleDateFormat("HH");
        String hour = hourFormat.format(currPoint.getArrivalDate());
        binding.etHhOfTripPoint.setText(hour);

        DateFormat minuteFormat = new SimpleDateFormat("mm");
        String minute = minuteFormat.format(currPoint.getArrivalDate());
        binding.etMmOfTripPoint.setText(minute);

        binding.rvParticipants.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvParticipants.setHasFixedSize(true);

        new Thread(() -> {
            try {
                //List<TripPointParticipant> participants = new TripPointParticipantDao(BaseConnection.getConnectionSource()).getParticipantsByTripPoint(currPoint);
                participants = new TripPointParticipantDao(BaseConnection.getConnectionSource()).getParticipantsByTripPoint(currPoint);


                Log.i("participants size", String.valueOf(participants.size()));
                getActivity().runOnUiThread(() -> {
                    participantsRecyclerViewAdapter = new ParticipantsRecyclerViewAdapter(participants, this);
                    binding.rvParticipants.setAdapter(participantsRecyclerViewAdapter);
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                this,
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String date = dayOfMonth + "-" + month +"-"+ year;
        binding.etDateOfTripPoint.setText(date);
    }

    @Override
    public void onParticipantClick(int position) {
        //participants.remove(position);
        participantsRecyclerViewAdapter.deleteParticipant(position);
        TripPointParticipant participant = participants.get(position);

        new Thread(() -> {
            try {
                TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
                tripPointDao.removeUserFromTripPoint(participant.getUser(), currPoint);
                Log.i("participant delete", "positon " + position);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }
}