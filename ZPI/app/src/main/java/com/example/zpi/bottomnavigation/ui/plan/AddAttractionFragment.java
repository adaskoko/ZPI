package com.example.zpi.bottomnavigation.ui.plan;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddAttractionBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointType;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointTypeDao;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddAttractionFragment extends Fragment implements DatePickerDialog.OnDateSetListener, OnConnectionFailedListener {

    private static final String TAG = "AddAttractionFragment";
    private FragmentAddAttractionBinding binding;
    private Trip currTrip;
    private TripPointLocation tripPointLocation = null;
//    private GoogleApiClient mGoogleApiClient;
//    protected GeoDataClient mGeoDataClient;
//    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
//            new LatLng(-40, -168), new LatLng(71, 136)
//    );
    private int iHour, iMinute;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = requireActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra(TRIP_KEY);
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_api_key), Locale.ENGLISH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddAttractionBinding.inflate(inflater, container, false);

        binding.nameTripPointET.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });
        binding.btnAddPrepPoint.setOnClickListener(c -> addAttraction());
        binding.hhOfTripPointET.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), (view, hourOfDay, minute) -> {
                iHour = hourOfDay;
                iMinute = minute;
                Calendar calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, iHour, iMinute);
                binding.hhOfTripPointET.setText(DateFormat.format("HH:mm", calendar));
            }, 12, 0, true);
            timePickerDialog.updateTime(iHour, iMinute);
            timePickerDialog.show();
        });
        binding.dateOfTripPointET.setOnClickListener(c -> showDatePickerDialog());
        hideSoftKeyboard();
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                binding.nameTripPointET.setText(place.getName());
                binding.adressOfTripPointET.setText(place.getAddress());
                tripPointLocation = new TripPointLocation(place.getId(), place.getLatLng().latitude, place.getLatLng().longitude, place.getAddress());
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

    private void addAttraction() {
        String title = binding.nameTripPointET.getText().toString();
        String address = binding.adressOfTripPointET.getText().toString();
        String sDate = binding.dateOfTripPointET.getText().toString();
        String time = binding.hhOfTripPointET.getText().toString();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm yyyy-MM-dd");
        Date arrivalDate = null;
        String date = time+" "+sDate;
        Log.i("add atraction date", date);

        try {
            arrivalDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (tripPointLocation == null){
            tripPointLocation = new TripPointLocation(null, 0.0, 0.0, address);
        }

        Date finalArrivalDate = arrivalDate;
        Date departureDate=new Date(finalArrivalDate.getYear(), finalArrivalDate.getMonth(), finalArrivalDate.getDay(), 23,59);
        new Thread(() -> {
            try {
                TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
                TripPointType tripPointType = new TripPointTypeDao(BaseConnection.getConnectionSource()).getAtrakcjaTripPointType();
                tripPointDao.createTripPoint(title, finalArrivalDate, departureDate, null, currTrip, tripPointLocation, tripPointType);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        NavHostFragment.findNavController(this).navigate(R.id.action_addAttraction_to_navigation_plan);
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
        binding.dateOfTripPointET.setText(date);
    }

    private void hideSoftKeyboard(){
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}