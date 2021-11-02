package com.example.zpi.bottomnavigation.ui.plan;

import static com.example.zpi.bottomnavigation.ui.plan.PlanFragment.PLAN_KEY;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddAttractionBinding;
import com.example.zpi.databinding.FragmentAttractionDetailsBinding;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointParticipantDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AttractionDetailsFragment extends Fragment {


    private TripPoint actPoint;
    FragmentAttractionDetailsBinding binding;
    private ListView list ;
    private ArrayAdapter<String> adapter ;

    public AttractionDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint = (TripPoint) getArguments().get("PLAN_KEY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAttractionDetailsBinding.inflate(inflater, container, false);
        //fillTextViews();
        binding.btnDeleteAttraction.setOnClickListener(c -> delete());
        binding.btnEditAttraction.setOnClickListener(c -> edit());
        return binding.getRoot();
    }

    private void fillTextViews(){
        binding.pointNameTV.setText(actPoint.getName());
        //binding.tv_pointAddress.setText(actPoint.getA)
        binding.tvPointHH.setText(actPoint.getArrivalDate().getHours());
        binding.tvPointMM.setText(actPoint.getArrivalDate().getMinutes());

        list = binding.lvParticipants;
        ArrayList<String> participants = (ArrayList<String>) getPointParticipants();
        adapter = new ArrayAdapter<>(getContext(), R.layout.user_spinner_row, participants);

        list.setAdapter(adapter);


    }

    private List<String> getPointParticipants(){
        List<String> participants = new ArrayList<>();

        new Thread(() -> {
            try {
                TripPointParticipantDao tpDao = new TripPointParticipantDao(BaseConnection.getConnectionSource());
                List<TripPointParticipant> tripPointParticipants = tpDao.getParticipantsByTripPoint(actPoint);
                if (tripPointParticipants != null && tripPointParticipants.size() != 0) {
                    for (TripPointParticipant tp : tripPointParticipants) {
                        User u = tp.getUser();
                        String currentRow = u.getName() + " " + u.getSurname() + "(" + u.getEmail() + ")";
                        participants.add(currentRow);
                    }
                }
                BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        return participants;
    }

    private void delete(){
        new Thread(() -> {
            try {
                TripPointDao pointDao = new TripPointDao(BaseConnection.getConnectionSource());
                pointDao.delete(actPoint);
                Log.i("todo", "usunieto todo");
                Log.i("todo", String.valueOf(actPoint == null));
                BaseConnection.closeConnection();
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
        Navigation.findNavController(getView()).navigate(R.id.action_attractionDetailsFragment_to_attractionEditFragment);
    }
}