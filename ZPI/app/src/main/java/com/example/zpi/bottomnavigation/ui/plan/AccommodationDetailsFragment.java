package com.example.zpi.bottomnavigation.ui.plan;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;
import static com.example.zpi.bottomnavigation.ui.plan.PlanFragment.PLAN_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAccomodationDetailsBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointLocationDao;

import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

public class AccommodationDetailsFragment extends Fragment {

    private TripPoint actPoint;
    FragmentAccomodationDetailsBinding binding;
    private Trip currTrip;

    public AccommodationDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint = (TripPoint) getArguments().get(PLAN_KEY);
            //currTrip = (Trip) getArguments().get(TRIP_KEY);
        }
        Intent intent = requireActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra(TRIP_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccomodationDetailsBinding.inflate(inflater, container, false);
        fillTextView();
        Log.i(getClass().getSimpleName(), "trip = null" + String.valueOf(currTrip == null));
        Log.i(getClass().getSimpleName(), "trip start date" + String.valueOf(currTrip.getStartDate() == null));
        Log.i(getClass().getSimpleName(), "trip end date" + String.valueOf(currTrip.getEndDate() == null));
        Date tripEndDate = currTrip.getEndDate();
        Date today = new Date();

        if(tripEndDate.before(today)){
            binding.btnDeleteAccomodation.setVisibility(View.INVISIBLE);
            binding.btnEditAccomodation.setVisibility(View.INVISIBLE);
        }
        binding.btnDeleteAccomodation.setOnClickListener(c->delete());
        binding.btnEditAccomodation.setOnClickListener(c->edit());
        return binding.getRoot();
    }

    private void fillTextView(){
        binding.accNameTV.setText(actPoint.getName());
        new Thread(() -> {
            try {
                TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
                String address = tripPointLocationDao.getLocationForTripPoint(actPoint).getAddress();
                getActivity().runOnUiThread(() -> binding.tvAccAddress.setText(address));
            } catch (SQLException throwables) {

            }
        }).start();
        binding.tvAccDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", actPoint.getArrivalDate()));
        binding.tvAccHH.setText(android.text.format.DateFormat.format("HH:mm", actPoint.getArrivalDate()));

        binding.tvAccHH2.setText(android.text.format.DateFormat.format("HH:mm", actPoint.getDepartureDate()));
        binding.tvAccDate2.setText(android.text.format.DateFormat.format("yyyy-MM-dd", actPoint.getDepartureDate()));
        binding.tvAccDetails.setText(actPoint.getRemarks());
    }

    private void delete(){
        new Thread(() -> {
            try {
                TripPointDao pointDao = new TripPointDao(BaseConnection.getConnectionSource());
                pointDao.delete(actPoint);
                Log.i("todo", "usunieto nocleg");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        NavHostFragment.findNavController(this).navigate(R.id.action_accomodationDetailsFragment_to_navigation_plan);
    }

    private void edit(){
        Bundle bundle = new Bundle();
        Log.i("putting", actPoint.getName());
        bundle.putSerializable(PLAN_KEY, actPoint);
        NavHostFragment.findNavController(this).navigate(R.id.action_accomodationDetailsFragment_to_accomodationEditFragment, bundle);
    }
}