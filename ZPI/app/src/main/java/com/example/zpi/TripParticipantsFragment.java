package com.example.zpi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.zpi.bottomnavigation.ui.todo.ToDoFragment;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentTripParticipantsBinding;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripPartcicipantDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TripParticipantsFragment extends Fragment {

    private FragmentTripParticipantsBinding binding;
    private Trip currTrip;
    private ListView listView ;
    private ArrayAdapter<String> adapter ;


    public TripParticipantsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currTrip = (Trip) getArguments().get(SingleTripFragment.TRIP_KEY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentTripParticipantsBinding.inflate(inflater, container, false);
        listView=(ListView) container.findViewById(R.id.participantsLV);
        getTripParticipants();
        binding.btnEdit.setOnClickListener(c-> addUsers());
        View root = binding.getRoot();
        return root;
    }

    public void getTripParticipants(){
        List<String> parts=new ArrayList<String>();

        new Thread(() -> {
            try {
                TripPartcicipantDao tpDao=new TripPartcicipantDao(BaseConnection.getConnectionSource());
                List<TripParticipant> tripParticipants=tpDao.getByTrip(currTrip);
                Log.i("tripparticiopants z dao", String.valueOf(tripParticipants.size()));
                if(tripParticipants!=null && tripParticipants.size()!=0) {
                    for (TripParticipant tp:tripParticipants) {
                        User u=tp.getUser();
                        Log.i("user z dao", String.valueOf(u.getEmail()));

                        String currentRow=u.getName()+" "+ u.getSurname()+"("+u.getEmail()+")";
                        parts.add(currentRow);
                    }
                }

                getActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.found_user_in_list, parts);
                    Log.i("parts z dao", String.valueOf(parts.size()));

                    //set adapter to listview
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        //return participants;
    }

    public void addUsers(){
        Intent intent=new Intent(getActivity(), InviteUsersActivity.class);
        intent.putExtra("CreateTrip", currTrip);
        startActivity(intent);
    }


}