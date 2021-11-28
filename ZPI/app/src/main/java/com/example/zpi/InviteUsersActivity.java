package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.zpi.bottomnavigation.BottomNavigationActivity;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripDao;
import com.example.zpi.repositories.TripParticipantDao;
import com.example.zpi.repositories.UserDao;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InviteUsersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, Serializable {

    TextView currentTripName;
    TextView currentTripDate;
    User loggedUser;
    Trip currentTrip;
    ListView participants;

    ListView list;
    SearchUserForNewConversationActivity.ListViewAdapter adapter;
    SearchView editSearch;
    ArrayList<User> arraylist = new ArrayList<>();
    TextView chosenUserTV;
    User chosenUser;
    View clickOutView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_users);

        list = findViewById(R.id.listview);
        chosenUserTV = findViewById(R.id.chosen_user);
        clickOutView = findViewById(R.id.clickOutView2);

        new Thread(() -> {
            try {
                UserDao userDao = new UserDao(BaseConnection.getConnectionSource());
                ArrayList<User> al = (ArrayList<User>) userDao.getAllUsers();
                runOnUiThread(() -> {
                    this.arraylist = al;
                    adapter = new SearchUserForNewConversationActivity.ListViewAdapter(this, arraylist);
                    list.setAdapter(adapter);
                    editSearch = findViewById(R.id.search);
                    editSearch.setIconified(false);
                    editSearch.setOnQueryTextListener(this);
                    editSearch.setOnClickListener(v -> {
                        list.setVisibility(View.VISIBLE);
                        adapter.filter(editSearch.getQuery().toString());
                    });
                    adapter.notifyDataSetChanged();
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        list.setOnItemClickListener((arg0, arg1, position, arg3) -> {
            chosenUser = (User) arg0.getAdapter().getItem(position);
            list.setVisibility(View.GONE);

            new Thread(() -> {
                try {
                    if(chosenUser!=null){
                        TripDao tripDao=new TripDao(BaseConnection.getConnectionSource());
                        tripDao.addRegularParticipant(currentTrip, chosenUser);
                        getTripParticipants();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }).start();
        });

        clickOutView.setOnClickListener(c -> {
            list.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        currentTripName=findViewById(R.id.tv_tripname);
        currentTripDate=findViewById(R.id.tv_tripdate);
        currentTrip = (Trip)getIntent().getSerializableExtra("CreateTrip");
        currentTripName.setText(currentTrip.getName());
        String dateRange=dateFormat.format(currentTrip.getStartDate())+" - "+dateFormat.format(currentTrip.getEndDate());
        currentTripDate.setText(dateRange);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!newText.equals("")) list.setVisibility(View.VISIBLE);
        else list.setVisibility(View.GONE);
        adapter.filter(newText);
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        participants=findViewById(R.id.lv_participants);
        getTripParticipants();
    }


    public void getTripParticipants(){
        List<String> parts=new ArrayList<>();

        new Thread(() -> {
            try {
                TripParticipantDao tpDao=new TripParticipantDao(BaseConnection.getConnectionSource());
                List<TripParticipant> tripParticipants=tpDao.getByTrip(currentTrip);
                if(tripParticipants.size()!=0) {
                    for (TripParticipant tp:tripParticipants) {
                        User u=tp.getUser();
                        String currentRow=u.getName()+" "+ u.getSurname()+"("+u.getEmail()+")";
                        parts.add(currentRow);
                    }
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.found_user_in_list, parts);
                    participants.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    public void goToCurrentTripMainPanel(View v){
        Intent intent = new Intent(this, BottomNavigationActivity.class);
        intent.putExtra("TRIP", currentTrip);
        startActivity(intent);
    }

    public void back(View v){
        finish();
    }

}