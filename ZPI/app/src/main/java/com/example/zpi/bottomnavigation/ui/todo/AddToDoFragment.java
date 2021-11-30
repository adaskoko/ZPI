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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.zpi.R;
import com.example.zpi.RegisterSuccesfulActivity;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddToDoBinding;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.TripDao;
import com.example.zpi.repositories.UserDao;
import com.j256.ormlite.stmt.query.In;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddToDoFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private FragmentAddToDoBinding binding;
    private User chosenUser;
    private Trip  currTrip;
    ImageButton addButton;
    RequestQueue mRequestQueue;
    private String URL="https://fcm.googleapis.com/fcm/send";
    private String serverKey="key="+"AAAATTz1BGM:APA91bFqP2Xnkl67JXawBGQ0tpMGiQFH9QPz1yBVYV6x5LT1_DOCUmCseexqFC0guffW7qXN_ke0DgOTujrRRmYw6CijP4H0cG4VpA8Rk6bf6ovPejnRfU8dRlCbzAQhyc6ZkPZCNljY";
    private String contentType= "application/json";

    public AddToDoFragment() {
        // Required empty public constructor
    }

    private String getTopicName(){
        String tripname=currTrip.getName();
        return tripname.replaceAll("\\s+","");
    }
    private void sendNotification() throws JSONException {
        mRequestQueue= Volley.newRequestQueue(getContext());
        JSONObject main=new JSONObject();
        main.put("to", "/topics/"+ getTopicName());
        JSONObject sub=new JSONObject();
        sub.put("title", "UWAGA");
        sub.put("message", "Dodato punkt przygotowań do "+ currTrip.getName());
        main.put("data", sub);

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header=new HashMap<>();
                header.put("content-type",contentType );
                header.put("authorization", serverKey);
                return header;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddToDoBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment


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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.dateOfTrip.setOnClickListener(c -> showDatePickerDialog());
        binding.btnAddPrepPoint.setOnClickListener(c -> addTodo());

        return binding.getRoot();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(currTrip.getEndDate().getTime());
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void addTodo() {
        String title = binding.nameOfTrip.getText().toString();
        String description = binding.descOfTrip.getText().toString();
        String deadline = binding.dateOfTrip.getText().toString();
        Date date = null;
        Log.i("to do date", deadline);

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
            if(currTrip.getEndDate().after(new Date())){
            if((date.equals(new Date()) || date.after(new Date())) && !date.after(currTrip.getEndDate())) {
                new Thread(() -> {
                    try {
                        PreparationPointDao pointDao = new PreparationPointDao(BaseConnection.getConnectionSource());
                        PreparationPoint point = new PreparationPoint(title, description, finalDate, chosenUser, currTrip);
                        pointDao.create(point);
                        Log.i("todo", "todo dodane");
                        try {
                            sendNotification();
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                        //BaseConnection.closeConnection();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }).start();
            }else{
                Toast.makeText(getContext(), "Niezgodność dat!", Toast.LENGTH_SHORT).show();
                binding.dateOfTrip.getText().clear();

                }
            }else{
                Toast.makeText(getContext(), "Brak możliwości edycji dla przeszłych wycieczek", Toast.LENGTH_SHORT).show();
                binding.dateOfTrip.getText().clear();
                }
        }
        NavHostFragment.findNavController(this).navigate(R.id.action_addToDoFragment_to_navigation_todo);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String date = year + "-" + month +"-"+ dayOfMonth;
        binding.dateOfTrip.setText(date);
    }
}