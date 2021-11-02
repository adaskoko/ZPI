package com.example.zpi.bottomnavigation.ui.totake;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.zpi.R;
import com.example.zpi.bottomnavigation.ui.todo.PersonSpinnerAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentToTakeThingEditBinding;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.ProductToTakeDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.util.List;

public class ToTakeThingEditFragment extends Fragment {

    private ProductToTake actPoint;
    private FragmentToTakeThingEditBinding binding;
    private Trip actTrip;
    private User chosenUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint=(ProductToTake) getArguments().get(ToTakeThingsFragment.TOTAKE_KEY);
        }
        Intent intent =getActivity().getIntent();
        actTrip=(Trip)intent.getSerializableExtra("TRIP");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentToTakeThingEditBinding.inflate(inflater, container, false);
        new Thread(() -> {
            try {
                List<User> userList = new UserDao(BaseConnection.getConnectionSource()).getUsersFromTrip(actTrip);
                Log.i("todo size fragemnt", String.valueOf(userList.size()));
                getActivity().runOnUiThread(() -> {
                    PersonSpinnerAdapter personAdapter = new PersonSpinnerAdapter(requireContext(), userList);
                    binding.spParticipants.setAdapter(personAdapter);
                });
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        fillEtitTexts();

        binding.spParticipants.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenUser=(User) parent.getItemAtPosition(position);
                String clickedUsr=chosenUser.getName();
                //??nie brakuje tu tego actPoint.setUser(chosenUser);
                Toast.makeText(getContext(),clickedUsr+" selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btnOk.setOnClickListener(c->saveToTake());
        return binding.getRoot();
    }

    private void fillEtitTexts(){
        binding.etToTakeName.setText(actPoint.getName());
        binding.cbDone.setChecked(actPoint.isDone());
    }

    private void saveToTake(){
        String title=binding.etToTakeName.getText().toString();
        Boolean isDone=binding.cbDone.isChecked();

        new Thread(() -> {
            try {
                ProductToTakeDao pointDao = new ProductToTakeDao(BaseConnection.getConnectionSource());
                actPoint.setName(title);
                actPoint.setDone(isDone);
                pointDao.update(actPoint);
                Log.i("todo", "todo edited");
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();


    }
}