package com.example.zpi.bottomnavigation.ui.plan;

import static com.example.zpi.bottomnavigation.ui.plan.PlanFragment.PLAN_KEY;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAccomodationDetailsBinding;
import com.example.zpi.models.TripPoint;
import com.example.zpi.repositories.TripPointDao;

import java.sql.SQLException;

public class AccomodationDetailsFragment extends Fragment {

    private TripPoint actPoint;
    FragmentAccomodationDetailsBinding binding;

    public AccomodationDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint = (TripPoint) getArguments().get(PLAN_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccomodationDetailsBinding.inflate(inflater, container, false);
        fillTextView();
        binding.btnDeleteAccomodation.setOnClickListener(c->delete());
        binding.btnEditAccomodation.setOnClickListener(c->edit());
        return binding.getRoot();
    }

    private void fillTextView(){
        binding.accNameTV.setText(actPoint.getName());
        //binding.tv_accAddress
        binding.tvAccDate.setText(actPoint.getArrivalDate().toString());
        binding.tvAccHH.setText(actPoint.getArrivalDate().getHours());
        binding.tvAccMM.setText(actPoint.getArrivalDate().getMinutes());

        binding.tvAccDate2.setText(actPoint.getDepartureDate().toString());
        binding.tvAccHH2.setText(actPoint.getDepartureDate().getHours());
        binding.tvAccMM2.setText(actPoint.getDepartureDate().getMinutes());

        binding.tvAccDetails.setText(actPoint.getRemarks());

    }

    private void delete(){
        new Thread(() -> {
            try {
                TripPointDao pointDao = new TripPointDao(BaseConnection.getConnectionSource());
                pointDao.delete(actPoint);
                Log.i("todo", "usunieto nocleg");
                BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        NavHostFragment.findNavController(this).navigate(R.id.action_accomodationDetailsFragment_to_navigation_plan);
    }

    private void edit(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(PLAN_KEY, actPoint);
        NavHostFragment.findNavController(this).navigate(R.id.action_accomodationDetailsFragment_to_accomodationEditFragment);
    }
}