package com.example.zpi.bottomnavigation.ui.plan;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddAccomodationBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointType;
import com.example.zpi.repositories.TripPointDao;
import com.example.zpi.repositories.TripPointTypeDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AddAccommodationFragment extends Fragment {

    private FragmentAddAccomodationBinding binding;
    private Trip currTrip;
    RequestQueue mRequestQueue;
    private String URL="https://fcm.googleapis.com/fcm/send";
    private String serverKey="key="+"AAAATTz1BGM:APA91bFqP2Xnkl67JXawBGQ0tpMGiQFH9QPz1yBVYV6x5LT1_DOCUmCseexqFC0guffW7qXN_ke0DgOTujrRRmYw6CijP4H0cG4VpA8Rk6bf6ovPejnRfU8dRlCbzAQhyc6ZkPZCNljY";
    private String contentType= "application/json";

    public AddAccommodationFragment() {
    }

    private void sendNotification() throws JSONException {
        mRequestQueue= Volley.newRequestQueue(getContext());
        JSONObject main=new JSONObject();
        main.put("to", "/topics/"+ currTrip.getName());
        JSONObject sub=new JSONObject();
        sub.put("title", "notification");
        sub.put("message", "Dodato nowy nocleg do "+ currTrip.getName());
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
        binding = FragmentAddAccomodationBinding.inflate(inflater, container, false);

        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");
        binding.btnAddAccPoint.setOnClickListener(c -> addAccommodation());
        return binding.getRoot();
    }

    private void addAccommodation() {
        String title = binding.nameAccET.getText().toString();
        String address = binding.adressOfAccET.getText().toString();
        String date_from = binding.dateOfAccET.getText().toString();
        String hour_from = binding.hhOfAccET.getText().toString();
        String minute_from = binding.mmOfAcctET.getText().toString();
        String date_to = binding.dateOfAccET2.getText().toString();
        String hour_to = binding.hhOfAccET2.getText().toString();
        String minute_to = binding.mmOfAcctET2.getText().toString();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");
        String dateFrom = hour_from+":"+minute_from+" "+date_from;
        String dateTo = hour_to+":"+minute_to+" "+date_to;

        Date dDateFrom = new Date();
        Date dDateTo = new Date();
        try {
            dDateFrom = dateFormat.parse(dateFrom);
            dDateTo = dateFormat.parse(dateTo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TripPointLocation location = new TripPointLocation(0.0, 0.0, address);

        Date finalDDateFrom = dDateFrom;
        Date finalDDateTo = dDateTo;
        new Thread(() -> {
            try {
                TripPointDao tripPointDao = new TripPointDao(BaseConnection.getConnectionSource());
                TripPointType tripPointType = new TripPointTypeDao(BaseConnection.getConnectionSource()).getNoclegTripPointType();
                tripPointDao.createTripPoint(title, finalDDateFrom, finalDDateTo, null, currTrip, location, tripPointType);
                sendNotification();
            } catch (SQLException | JSONException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        NavHostFragment.findNavController(this).navigate(R.id.action_addAccomodation_to_navigation_plan);
    }

    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);    }
}