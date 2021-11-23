package com.example.zpi.bottomnavigation.ui.plan;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.zpi.bottomnavigation.ui.plan.PlanFragment.PLAN_KEY;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.zpi.R;
import com.example.zpi.bottomnavigation.BottomNavigationActivity;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAccomodationEditBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointLocationDao;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;


import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AccommodationEditFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "AccommodationEditFragment";
    private FragmentAccomodationEditBinding binding;
    private Trip actTrip;
    private TripPoint actPoint;
    private TripPointLocation accommodationLocation = null;
    private int iYear, iMonth, iDay, iHour, iMinute, iFlag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint = (TripPoint) getArguments().get(PlanFragment.PLAN_KEY);
            Log.i("act point", "act point");
        }
        //Intent intent = new Intent();
        //actTrip = (Trip) intent.getSerializableExtra(BottomNavigationActivity.TRIP_KEY);
        Intent intent = requireActivity().getIntent();
        actTrip = (Trip) intent.getSerializableExtra(BottomNavigationActivity.TRIP_KEY);
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_api_key), Locale.ENGLISH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccomodationEditBinding.inflate(inflater, container, false);
        fillEditText();
        init();
        binding.btnAddPrepPoint.setOnClickListener(v -> save());
        return binding.getRoot();
    }

    private void init() {
        binding.etNameAcc.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });
        binding.etDateOfAcc.setOnClickListener(v -> {
            iFlag = 1;
            showDatePickerDialog();
        });
        binding.etDateOfAcc2.setOnClickListener(v -> {
            iFlag = 2;
            showDatePickerDialog();
        });
//        binding.etDateOfAcc.setOnClickListener(v -> {
//            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
//                iYear = year;
//                iMonth = month;
//                iDay = dayOfMonth;
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(iYear, iMonth, iDay);
//                binding.etDateOfAcc.setText(android.text.format.DateFormat.format("yyyy-MM-dd", calendar));
//            },Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//            datePickerDialog.updateDate(iYear, iMonth, iDay);
//            datePickerDialog.show();
//        });
//        binding.etDateOfAcc2.setOnClickListener(v -> {
//            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
//                iYear = year;
//                iMonth = month;
//                iDay = dayOfMonth;
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(iYear, iMonth, iDay);
//                binding.etDateOfAcc2.setText(android.text.format.DateFormat.format("yyyy-MM-dd", calendar));
//            },Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//            datePickerDialog.updateDate(iYear, iMonth, iDay);
//            datePickerDialog.show();
//        });

        binding.etHhOfAcc.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), (view, hourOfDay, minute) -> {
                iHour = hourOfDay;
                iMinute = minute;
                Calendar calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, iHour, iMinute);
                binding.etHhOfAcc.setText(android.text.format.DateFormat.format("HH:mm", calendar));
            }, 12, 0, true);
            timePickerDialog.updateTime(iHour, iMinute);
            timePickerDialog.show();
        });
        binding.etHhOfAcc2.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), (view, hourOfDay, minute) -> {
                iHour = hourOfDay;
                iMinute = minute;
                Calendar calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, iHour, iMinute);
                binding.etHhOfAcc2.setText(android.text.format.DateFormat.format("HH:mm", calendar));
            }, 12, 0, true);
            timePickerDialog.updateTime(iHour, iMinute);
            timePickerDialog.show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                binding.etNameAcc.setText(place.getName());
                binding.etAddressOfAcc.setText(place.getAddress());
                //accommodationLocation = new TripPointLocation(place.getId(), place.getLatLng().latitude, place.getLatLng().longitude, place.getAddress());
                new Thread(() -> {
                    try {
                        TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
                        accommodationLocation = tripPointLocationDao.getLocationForTripPoint(actPoint);
                        accommodationLocation.setGoogleID(place.getId());
                        accommodationLocation.setLatitude(place.getLatLng().latitude);
                        accommodationLocation.setLongitude(place.getLatLng().longitude);
                        accommodationLocation.setAddress(place.getAddress());
                        tripPointLocationDao.update(accommodationLocation);
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
                // The user canceled the operation.
                Log.i(TAG, "The user canceled the operation");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void save() {
        String title = binding.etNameAcc.getText().toString();
        //String address = binding.etAddressOfAcc.getText().toString();
        String desc = binding.etDescAcc.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");
        String arrivalS = binding.etHhOfAcc.getText().toString()+" "+binding.etDateOfAcc.getText().toString();
        String departureS = binding.etHhOfAcc2.getText().toString()+" "+binding.etDateOfAcc2.getText().toString();
        Date arrivalDate = null;
        Date departureDate = null;
        try {
            arrivalDate = dateFormat.parse(arrivalS);
            departureDate = dateFormat.parse(departureS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date finalArrivalDate = arrivalDate;
        Date finalDepartureDate = departureDate;
        actPoint.setName(title);
        actPoint.setArrivalDate(finalArrivalDate);
        actPoint.setDepartureDate(finalDepartureDate);
        actPoint.setRemarks(desc);
        new Thread(() -> {
            try {
//                if (accommodationLocation == null) {
//                    TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
//                    accommodationLocation = tripPointLocationDao.getLocationForTripPoint(actPoint);
//                }
//                accommodationLocation.setAddress(address);
                //actPoint.setTripPointLocation(accommodationLocation);
                TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
                tripPointDao.update(actPoint);
                Log.i("edit point", "point edited");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        NavHostFragment.findNavController(this).navigate(R.id.action_accommodationEditFragment_to_navigation_plan);
    }

    private void fillEditText() {
        binding.etNameAcc.setText(actPoint.getName());
        binding.etAddressOfAcc.setText("Brak");
        new Thread(() -> {
            try {
                TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
                String address = tripPointLocationDao.getLocationForTripPoint(actPoint).getAddress();
                getActivity().runOnUiThread(() -> binding.etAddressOfAcc.setText(address));
            } catch (SQLException throwables) {

            }
        }).start();
        binding.etDateOfAcc.setText(android.text.format.DateFormat.format("yyyy-MM-dd", actPoint.getArrivalDate()));
        binding.etDateOfAcc2.setText(android.text.format.DateFormat.format("yyyy-MM-dd", actPoint.getDepartureDate()));
        binding.etHhOfAcc.setText(android.text.format.DateFormat.format("HH:mm", actPoint.getArrivalDate()));
        binding.etHhOfAcc2.setText(android.text.format.DateFormat.format("HH:mm", actPoint.getDepartureDate()));
        binding.etDescAcc.setText(actPoint.getRemarks());
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
        if (iFlag == 1) {
            binding.etDateOfAcc.setText(date);
        }
        else if (iFlag == 2) {
            binding.etDateOfAcc2.setText(date);
        }
    }
}