package com.example.zpi.bottomnavigation.ui.totake;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.zpi.R;
import com.example.zpi.bottomnavigation.ui.todo.PersonSpinnerAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddToTakeThingBinding;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.ProductToTakeDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.util.List;


public class AddToTakeThingFragment extends Fragment {

    private FragmentAddToTakeThingBinding binding;
    private User chosenUser;
    private Trip currTrip;

    public AddToTakeThingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddToTakeThingBinding.inflate(inflater, container, false);

        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");

        new Thread(() -> {
            try {
                List<User> userList = new UserDao(BaseConnection.getConnectionSource()).getUsersFromTrip(currTrip);
                Log.i("todo size fragemnt", String.valueOf(userList.size()));
                getActivity().runOnUiThread(() -> {
                    PersonSpinnerAdapter personAdapter = new PersonSpinnerAdapter(requireContext(), userList);
                    binding.assignedTo.setAdapter(personAdapter);
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        binding.assignedTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenUser = (User) parent.getItemAtPosition(position);
                String clickedUSer = chosenUser.getName();
                Toast.makeText(getContext(), clickedUSer + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnAddToTakeThing.setOnClickListener(c -> addToTakeThing());
        return binding.getRoot();
    }

    private void addToTakeThing() {
        String name = binding.nameOfThingToTakeET.getText().toString();
        new Thread(() -> {
            try {
                ProductToTakeDao productDao = new ProductToTakeDao(BaseConnection.getConnectionSource());
                ProductToTake product = new ProductToTake(name, chosenUser, currTrip);
                productDao.create(product);
                Log.i("toTake", "to take dodane");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        NavHostFragment.findNavController(this).navigate(R.id.action_addToTakeThingFragment_to_navigation_to_take_things);
    }
}