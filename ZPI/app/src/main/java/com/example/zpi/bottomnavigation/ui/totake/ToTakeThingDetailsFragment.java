package com.example.zpi.bottomnavigation.ui.totake;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;
import static com.example.zpi.bottomnavigation.ui.totake.ToTakeThingsFragment.TOTAKE_KEY;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentToTakeThingDetailsBinding;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.ProductToTakeDao;

import java.sql.SQLException;
import java.util.Date;


public class ToTakeThingDetailsFragment extends Fragment {

    private ProductToTake actPoint;
    FragmentToTakeThingDetailsBinding binding;
    private Trip currTrip;

    public ToTakeThingDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint = (ProductToTake) getArguments().get(TOTAKE_KEY);
            currTrip=(Trip) getArguments().get(TRIP_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentToTakeThingDetailsBinding.inflate(inflater, container, false);
        fillTextViews();
        Date tripEndDate=currTrip.getEndDate();
        Date today=new Date();

        if(tripEndDate.before(today)){
            binding.btnDeteleToTake.setVisibility(View.INVISIBLE);
            binding.btnEditToTake.setVisibility(View.INVISIBLE);
        }
        binding.btnDeteleToTake.setOnClickListener(c->delete());
        binding.btnEditToTake.setOnClickListener(c->edit());
        return binding.getRoot();
    }

    private void fillTextViews(){
        binding.productNameTV.setText(actPoint.getName());
        binding.tvPrPersonResponsible.setText(actPoint.getUser().getName());
        binding.cbDone.setChecked(actPoint.isDone());
        binding.cbDone.setEnabled(false);
    }

    private void delete(){
        new Thread(() -> {
            try {
                ProductToTakeDao pointDao = new ProductToTakeDao(BaseConnection.getConnectionSource());
                pointDao.delete(actPoint);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        NavHostFragment.findNavController(this).navigate(R.id.action_toTakeThingsDetailsFragment_to_navigation_to_take_things);
    }

    private void edit(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(TOTAKE_KEY, actPoint);
        NavHostFragment.findNavController(this).navigate(R.id.action_toTakeThingDetailsFragment_to_toTakeThingsEditFragment, bundle);
    }
}