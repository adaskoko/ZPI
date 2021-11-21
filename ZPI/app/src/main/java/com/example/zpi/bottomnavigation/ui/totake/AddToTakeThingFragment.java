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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.zpi.R;
import com.example.zpi.bottomnavigation.ui.todo.PersonSpinnerAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentAddToTakeThingBinding;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.PreparationPointDao;
import com.example.zpi.repositories.ProductToTakeDao;
import com.example.zpi.repositories.TripDao;
import com.example.zpi.repositories.UserDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddToTakeThingFragment extends Fragment {

    private FragmentAddToTakeThingBinding binding;
    private User chosenUser;
    private Trip currTrip;
    RequestQueue mRequestQueue;
    private String URL="https://fcm.googleapis.com/fcm/send";
    private String serverKey="key="+"AAAATTz1BGM:APA91bFqP2Xnkl67JXawBGQ0tpMGiQFH9QPz1yBVYV6x5LT1_DOCUmCseexqFC0guffW7qXN_ke0DgOTujrRRmYw6CijP4H0cG4VpA8Rk6bf6ovPejnRfU8dRlCbzAQhyc6ZkPZCNljY";
    private String contentType= "application/json";

    public AddToTakeThingFragment() {
        // Required empty public constructor
    }
    private void sendNotification() throws JSONException {
        mRequestQueue= Volley.newRequestQueue(getContext());
        JSONObject main=new JSONObject();
        main.put("to", "/topics/"+ currTrip.getName());
        JSONObject sub=new JSONObject();
        sub.put("title", "notification");
        sub.put("message", "Dodano rzecz do spakowania: "+ currTrip.getName());
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
        // Inflate the layout for this fragment
        binding = FragmentAddToTakeThingBinding.inflate(inflater, container, false);

        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");

        new Thread(() -> {
            try {
//                currTrip = new TripDao(BaseConnection.getConnectionSource()).queryForEq("ID", 1).get(0);
                List<User> userList = new UserDao(BaseConnection.getConnectionSource()).getUsersFromTrip(currTrip);
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
                sendNotification();
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        }).start();
        NavHostFragment.findNavController(this).navigate(R.id.action_addToTakeThingFragment_to_navigation_to_take_things);
    }
}