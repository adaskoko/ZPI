package com.example.zpi.bottomnavigation.ui.plan;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.zpi.R;
import com.example.zpi.bottomnavigation.BottomNavigationActivity;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAttractionEditBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointParticipant;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointLocationDao;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AttractionEditFragment extends Fragment implements DatePickerDialog.OnDateSetListener, ParticipantsRecyclerViewAdapter.OnParticipantListener {

    private FragmentAttractionEditBinding binding;
    private Trip currTrip;
    private TripPoint currPoint;
    private ParticipantsRecyclerViewAdapter participantsRecyclerViewAdapter;
    private List<TripPointParticipant> participants;
    private int iHour, iMinute;
    private TripPointLocation tripPointLocation = null;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "AttractionFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currPoint = (TripPoint) getArguments().get(PlanFragment.PLAN_KEY);
        }
        Intent intent = requireActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra(BottomNavigationActivity.TRIP_KEY);
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_api_key), Locale.ENGLISH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAttractionEditBinding.inflate(inflater, container, false);
        fillEditTexts();
        binding.etNameTripPoint.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });
        binding.etDateOfTripPoint.setOnClickListener(c -> showDatePickerDialog());
        binding.etHhOfTripPoint.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), (view, hourOfDay, minute) -> {
                iHour = hourOfDay;
                iMinute = minute;
                Calendar calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, iHour, iMinute);
                binding.etHhOfTripPoint.setText(android.text.format.DateFormat.format("HH:mm", calendar));
            }, 12, 0, true);
            timePickerDialog.updateTime(iHour, iMinute);
            timePickerDialog.show();
        });
        binding.btnAddPrepPoint.setOnClickListener(c -> save());
        return binding.getRoot();
    }

    private void save() {
        String title = binding.etNameTripPoint.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");
        String date = binding.etHhOfTripPoint.getText().toString()+" "+ binding.etDateOfTripPoint.getText().toString();
        currPoint.setName(title);
        try {
            currPoint.setArrivalDate(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
                tripPointDao.update(currPoint);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        NavHostFragment.findNavController(this).navigate(R.id.action_attractionEditFragment_to_navigation_plan);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                binding.etNameTripPoint.setText(place.getName());
                binding.etAdressOfTripPoint.setText(place.getAddress());
                //tripPointLocation = new TripPointLocation(place.getId(), place.getLatLng().latitude, place.getLatLng().longitude, place.getAddress());
                new Thread(() -> {
                    try {
                        TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
                        tripPointLocation = tripPointLocationDao.getLocationForTripPoint(currPoint);
                        tripPointLocation.setGoogleID(place.getId());
                        tripPointLocation.setLatitude(place.getLatLng().latitude);
                        tripPointLocation.setLongitude(place.getLatLng().longitude);
                        tripPointLocation.setAddress(place.getAddress());
                        tripPointLocationDao.update(tripPointLocation);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }).start();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng().latitude + "; " + place.getLatLng().longitude);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "The user canceled the operation");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fillEditTexts() {
        binding.etNameTripPoint.setText(currPoint.getName());
        binding.etAdressOfTripPoint.setText("Brak");
        new Thread(() -> {
            try {
                TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
                String address = tripPointLocationDao.getLocationForTripPoint(currPoint).getAddress();
                getActivity().runOnUiThread(() -> binding.etAdressOfTripPoint.setText(address));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        binding.etDateOfTripPoint.setText(android.text.format.DateFormat.format("yyyy-MM-dd", currPoint.getArrivalDate()));
        binding.etHhOfTripPoint.setText(android.text.format.DateFormat.format("HH:mm", currPoint.getArrivalDate()));

    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String date = year + "-" + month +"-"+ dayOfMonth;
        binding.etDateOfTripPoint.setText(date);
    }

    @Override
    public void onParticipantClick(int position) {
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