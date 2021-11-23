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
import android.view.WindowManager;
import android.widget.DatePicker;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddAccomodationBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointType;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointTypeDao;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class AddAccommodationFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "AddAccommodationFragment";
    private FragmentAddAccomodationBinding binding;
    private Trip currTrip;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private TripPointLocation accommodationLocation = null;
    private int iYear, iMonth, iDay, iHour, iMinute, iFlag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_api_key), Locale.ENGLISH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddAccomodationBinding.inflate(inflater, container, false);
        init();
        binding.btnAddAccPoint.setOnClickListener(c -> addAccommodation());
        hideSoftKeyboard();
        return binding.getRoot();
    }

    private void init() {
        binding.nameAccET.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });
        binding.dateOfAccET.setOnClickListener(v -> {
            iFlag = 1;
            showDatePickerDialog();
        });
        binding.dateOfAccET2.setOnClickListener(v -> {
            iFlag = 2;
            showDatePickerDialog();
        });
//        binding.dateOfAccET.setOnClickListener(v -> {
//            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
//                iYear = year;
//                iMonth = month;
//                iDay = dayOfMonth;
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(iYear, iMonth, iDay);
//                binding.dateOfAccET.setText(android.text.format.DateFormat.format("yyyy-MM-dd", calendar));
//            },Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//            datePickerDialog.updateDate(iYear, iMonth, iDay);
//            datePickerDialog.show();
//        });
//        binding.dateOfAccET2.setOnClickListener(v -> {
//            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
//                iYear = year;
//                iMonth = month;
//                iDay = dayOfMonth;
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(iYear, iMonth, iDay);
//                binding.dateOfAccET2.setText(android.text.format.DateFormat.format("yyyy-MM-dd", calendar));
//            },Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//            datePickerDialog.updateDate(iYear, iMonth, iDay);
//            datePickerDialog.show();
//        });

        binding.hhOfAccET.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), (view, hourOfDay, minute) -> {
                iHour = hourOfDay;
                iMinute = minute;
                Calendar calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, iHour, iMinute);
                binding.hhOfAccET.setText(android.text.format.DateFormat.format("HH:mm", calendar));
            }, 12, 0, true);
            timePickerDialog.updateTime(iHour, iMinute);
            timePickerDialog.show();
        });
        binding.hhOfAccET2.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), (view, hourOfDay, minute) -> {
                iHour = hourOfDay;
                iMinute = minute;
                Calendar calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, iHour, iMinute);
                binding.hhOfAccET2.setText(android.text.format.DateFormat.format("HH:mm", calendar));
            }, 12, 0, true);
            timePickerDialog.updateTime(iHour, iMinute);
            timePickerDialog.show();
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(currTrip.getEndDate().getTime());
        datePickerDialog.getDatePicker().setMinDate(currTrip.getStartDate().getTime());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String date = year + "-" + month +"-"+ dayOfMonth;
        if (iFlag == 1) {
            binding.dateOfAccET.setText(date);
        }
        else if (iFlag == 2) {
            binding.dateOfAccET2.setText(date);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                binding.nameAccET.setText(place.getName());
                binding.addressOfAccET.setText(place.getAddress());
                accommodationLocation = new TripPointLocation(place.getId(), place.getLatLng().latitude, place.getLatLng().longitude, place.getAddress());
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng().latitude + "; " + place.getLatLng().longitude);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "The user canceled the operation");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addAccommodation() {
        String title = binding.nameAccET.getText().toString();
        String date_from = binding.dateOfAccET.getText().toString();
        String hour_from = binding.hhOfAccET.getText().toString();
        String date_to = binding.dateOfAccET2.getText().toString();
        String hour_to = binding.hhOfAccET2.getText().toString();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");
        String dateFrom = hour_from+" "+date_from;
        String dateTo = hour_to+" "+date_to;

        Date dDateFrom = new Date();
        Date dDateTo = new Date();
        try {
            dDateFrom = dateFormat.parse(dateFrom);
            dDateTo = dateFormat.parse(dateTo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date finalDDateFrom = dDateFrom;
        Date finalDDateTo = dDateTo;
        new Thread(() -> {
            try {
                TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
                TripPointType tripPointType = new TripPointTypeDao(BaseConnection.getConnectionSource()).getNoclegTripPointType();
                tripPointDao.createTripPoint(title, finalDDateFrom, finalDDateTo, null, currTrip, accommodationLocation, tripPointType);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        NavHostFragment.findNavController(this).navigate(R.id.action_addAccomodation_to_navigation_plan);
    }

    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}