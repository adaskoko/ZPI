package com.example.zpi.bottomnavigation.ui.plan;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;
import static com.example.zpi.bottomnavigation.ui.plan.PlanFragment.PLAN_KEY;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddAttractionBinding;
import com.example.zpi.databinding.FragmentAttractionDetailsBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointLocationDao;
import com.example.zpi.repositories.TripPointParticipantDao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AttractionDetailsFragment extends Fragment {


    private TripPoint actPoint;
    FragmentAttractionDetailsBinding binding;
    private Trip currTrip;

    public AttractionDetailsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint = (TripPoint) getArguments().get(PLAN_KEY);
            currTrip=(Trip) getArguments().get(TRIP_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAttractionDetailsBinding.inflate(inflater, container, false);
        fillTextViews();
        Date tripEndDate=currTrip.getEndDate();
        Date today=new Date();

        if(tripEndDate.before(today)){
            binding.btnDeleteAttraction.setVisibility(View.INVISIBLE);
            binding.btnEditAttraction.setVisibility(View.INVISIBLE);
        }
        binding.btnDeleteAttraction.setOnClickListener(c -> delete());
        binding.btnEditAttraction.setOnClickListener(c -> edit());
        return binding.getRoot();
    }

    private void fillTextViews(){
        binding.pointNameTV.setText(actPoint.getName());
//        binding.tvPointAddress.setText(actPoint.getTripPointLocation().getAddress());
        binding.tvPointAddress.setText("Brak");
        new Thread(() -> {
            try {
                TripPointLocationDao tripPointLocationDao = new TripPointLocationDao(BaseConnection.getConnectionSource());
                String address = tripPointLocationDao.getLocationForTripPoint(actPoint).getAddress();
                getActivity().runOnUiThread(() -> binding.tvPointAddress.setText(address));
            } catch (SQLException throwables) {

            }
        }).start();

        binding.tvPointDate.setText(DateFormat.format("yyyy-MM-dd", actPoint.getArrivalDate()));
        binding.tvPointHh.setText(DateFormat.format("HH:mm", actPoint.getArrivalDate()));

    }

    private void delete(){
        new Thread(() -> {
            try {
                TripPointDao pointDao = new TripPointDao(BaseConnection.getConnectionSource());
                pointDao.delete(actPoint);
                Log.i("todo", "usunieto todo");
                Log.i("todo", String.valueOf(actPoint == null));

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        NavHostFragment.findNavController(this).navigate(R.id.action_attractionDetailsFragment_to_navigation_plan);
    }

    private void edit(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(PLAN_KEY, actPoint);
        Log.i("attr", "edit");
        NavHostFragment.findNavController(this).navigate(R.id.action_attractionDetailsFragment_to_attractionEditFragment, bundle);
    }
}