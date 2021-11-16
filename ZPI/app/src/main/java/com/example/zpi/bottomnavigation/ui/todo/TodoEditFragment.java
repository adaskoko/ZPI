package com.example.zpi.bottomnavigation.ui.todo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.databinding.FragmentTodoEditBinding;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TodoEditFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    
    private PreparationPoint actPoint;
    private FragmentTodoEditBinding binding;
    private Trip actTrip;
    private User chosenUser;
    User loggedUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint = (PreparationPoint) getArguments().get(ToDoFragment.TODO_KEY);
        }
        Intent intent = getActivity().getIntent();
        actTrip = (Trip) intent.getSerializableExtra("TRIP");
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoEditBinding.inflate(inflater, container, false);
        new Thread(() -> {
            try {
                List<User> userList = new UserDao(BaseConnection.getConnectionSource()).getUsersFromTrip(actTrip);
                Log.i("todo size fragemnt", String.valueOf(userList.size()));
                getActivity().runOnUiThread(() -> {
                    PersonSpinnerAdapter personAdapter = new PersonSpinnerAdapter(requireContext(), userList);
                    binding.assignedTo.setAdapter(personAdapter);
                });
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        //fillEditText();
        refreshResponsiblePeron();

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
        binding.tvPontDate.setOnClickListener(c -> showDatePickerDialog());
        binding.btnConfirm.setOnClickListener(c -> saveTodo());
        return binding.getRoot();
    }

    private void saveTodo() {
        String title = binding.etTodoName.getText().toString();
        String description = binding.etTodoDesc.getText().toString();
        String deadline = binding.tvPontDate.getText().toString();
        Boolean isDone = binding.cbDone.isChecked();
        Date date = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = dateFormat.parse(deadline);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null  && chosenUser == null) {
            Log.i("todo", "todo zle");
        } else {
            Date finalDate = date;
            new Thread(() -> {
                try {
                    PreparationPointDao pointDao = new PreparationPointDao(BaseConnection.getConnectionSource());
                    actPoint.setName(title);
                    actPoint.setDescription(description);
                    actPoint.setDeadline(finalDate);
                    actPoint.setDone(isDone);
                    actPoint.setUser(chosenUser);
                    pointDao.update(actPoint);
                    Log.i("todo", "todo edited");
                    //BaseConnection.closeConnection();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }).start();
        }
        NavHostFragment.findNavController(this).navigate(R.id.action_todoEditFragment_to_navigation_todo);
    }

    private void fillEditText() {
        binding.etTodoName.setText(actPoint.getName());
        binding.etTodoDesc.setText(actPoint.getDescription());
        binding.cbDone.setChecked(actPoint.isDone());
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(actPoint.getDeadline());
        binding.tvPontDate.setText(date);
    }

    private void refreshResponsiblePeron(){
        User responsible=actPoint.getUser();
        new Thread(()->{
            try {
                UserDao udao=new UserDao(BaseConnection.getConnectionSource());
                udao.refresh(responsible);
                Log.i("resp", String.valueOf(responsible.getID()));
                Log.i("logged", String.valueOf(loggedUser.getID()));
                if(responsible.getID()!=loggedUser.getID()){
                    Log.i("disabling", "disable chbx");
                    binding.cbDone.setEnabled(false);
                }
                getActivity().runOnUiThread(()->{
                    fillEditText();
                });
            }catch (SQLException throwables){
                throwables.printStackTrace();
            }
        }).start();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                this,
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String date = dayOfMonth + "-" + month +"-"+ year;
        binding.tvPontDate.setText(date);
    }
}