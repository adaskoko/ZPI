package com.example.zpi.bottomnavigation.ui.plan;

import static com.example.zpi.bottomnavigation.ui.plan.PlanFragment.PLAN_KEY;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

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


import java.sql.SQLException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AccommodationEditFragment extends Fragment implements View.OnClickListener {

    private FragmentAccomodationEditBinding binding;
    private Trip actTrip;
    private TripPoint actPoint;

    private DatePickerDialogFragment datePickerDialogFragment;

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
        datePickerDialogFragment = new DatePickerDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccomodationEditBinding.inflate(inflater, container, false);
        fillEditText();
        Log.i("ocvh", String.valueOf(actPoint.equals(null)));
        //binding.etDateOfAcc.setOnClickListener(v -> showDatePickerDialog());
        //binding.etDateOfAcc2.setOnClickListener(v -> showDatePickerDialog());
        binding.etDateOfAcc.setOnClickListener(this);
        binding.etDateOfAcc2.setOnClickListener(this);
        binding.btnAddPrepPoint.setOnClickListener(v -> save());
        return binding.getRoot();
    }

    private void save() {
        String title = binding.etNameAcc.getText().toString();
        String address = binding.etAdressOfAcc.getText().toString();
        String desc = binding.etDescAcc.getText().toString();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String arrivalS = binding.etHhOfAcc.getText().toString()+":"+binding.etMmOfAcct.getText().toString()+" "+binding.etDateOfAcc.getText().toString();
        String departureS = binding.etHhOfAcc2.getText().toString()+":"+binding.etMmOfAcct2.getText().toString()+" "+binding.etDateOfAcc2.getText().toString();
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
        new Thread(() -> {
            try {
                TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
                actPoint.setName(title);
                actPoint.setArrivalDate(finalArrivalDate);
                actPoint.setDepartureDate(finalDepartureDate);
                TripPointLocation tripPointLocation = actPoint.getTripPointLocation();
                tripPointLocation.setAddress(address);
                actPoint.setTripPointLocation(tripPointLocation);
                actPoint.setRemarks(desc);
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
        binding.etAdressOfAcc.setText(actPoint.getTripPointLocation().getAddress());
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String arrivalDate = dateFormat.format(actPoint.getArrivalDate());
        String departureDate = dateFormat.format(actPoint.getDepartureDate());
        DateFormat hourFormat = new SimpleDateFormat("HH");
        DateFormat minuteFormat = new SimpleDateFormat("mm");
        String arrivalHour = hourFormat.format(actPoint.getArrivalDate());
        String arrivalMinute = minuteFormat.format(actPoint.getArrivalDate());
        String departureHour = hourFormat.format(actPoint.getDepartureDate());
        String departureMinute = minuteFormat.format(actPoint.getDepartureDate());
        binding.etDateOfAcc.setText(arrivalDate);
        binding.etHhOfAcc.setText(arrivalHour);
        binding.etMmOfAcct.setText(arrivalMinute);
        binding.etDateOfAcc2.setText(departureDate);
        binding.etMmOfAcct2.setText(departureHour);
        binding.etMmOfAcct2.setText(departureMinute);
        binding.etDescAcc.setText(actPoint.getRemarks());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.et_dateOfAcc) {
            datePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_START_DATE);
            datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        } else if (id == R.id.et_dateOfAcc2) {
            datePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_END_DATE);
            datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }

    public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public static final int FLAG_START_DATE = 0;
        public static final int FLAG_END_DATE = 1;

        private int flag = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void setFlag(int i) {
            flag = i;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (flag == FLAG_START_DATE) {
                binding.etDateOfAcc.setText(format.format(calendar.getTime()));
            } else if (flag == FLAG_END_DATE) {
                binding.etDateOfAcc2.setText(format.format(calendar.getTime()));
            }
        }
    }
}