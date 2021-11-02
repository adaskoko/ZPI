package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripDao;
import com.example.zpi.repositories.TripPartcicipantDao;
import com.example.zpi.repositories.UserDao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InviteUsersActivity extends AppCompatActivity implements Serializable {

    TextView currentTripName;
    TextView currentTripDate;
    User loggedUser;
    Trip currentTrip;
    ListView participants;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_users);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        currentTripName=findViewById(R.id.tv_tripname);
        currentTripDate=findViewById(R.id.tv_tripdate);
        currentTrip = (Trip)getIntent().getSerializableExtra("CreateTrip");
        //currentTrip=getTripFromDatabase(createdTrip);
        currentTripName.setText(currentTrip.getName());
        String dateRange=currentTrip.getStartDate().toString()+" - "+currentTrip.getEndDate().toString();
        currentTripDate.setText(dateRange);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //list view of participants
        participants=findViewById(R.id.lv_participants);
        /*List<String> tripPatricipants=*/
        getTripParticipants();
        //Log.i("participants", String.valueOf(tripPatricipants.size()));

    }

    public void searchUsers(View v){
        EditText nameAndSurname=findViewById(R.id.et_personName);
        String[] input=nameAndSurname.getText().toString().split("\\s+");
        Intent intent=new Intent(this, MatchingUsersActivity.class);
        intent.putExtra("MATCH", input);
        intent.putExtra("TRIP", currentTrip);
        startActivity(intent);
        nameAndSurname.getText().clear();
    }

    private Trip getTripFromDatabase(Trip t){
        List<Trip> trips=new ArrayList<Trip>();
        new Thread(() -> {
            try {
                TripDao tripDao =new TripDao(BaseConnection.getConnectionSource());
                Trip foundTrip= (Trip) tripDao.getTripByNameAndDate(t.getName(), t.getStartDate(), t.getEndDate());
                if(foundTrip!=null){
                    trips.add(foundTrip);
                }
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        if(trips.size()!=0){
            return trips.get(0);
        }else{
            return null;
        }
    }

    public void getTripParticipants(){
        List<String> parts=new ArrayList<String>();

        new Thread(() -> {
            try {
                TripPartcicipantDao tpDao=new TripPartcicipantDao(BaseConnection.getConnectionSource());
                List<TripParticipant> tripParticipants=tpDao.getByTrip(currentTrip);
                Log.i("tripparticiopants z dao", String.valueOf(tripParticipants.size()));
                if(tripParticipants!=null && tripParticipants.size()!=0) {
                    for (TripParticipant tp:tripParticipants) {
                        User u=tp.getUser();
                        Log.i("user z dao", String.valueOf(u.getEmail()));

                        String currentRow=u.getName()+" "+ u.getSurname()+"("+u.getEmail()+")";
                        parts.add(currentRow);
                    }
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.found_user_in_list, parts);
                    Log.i("parts z dao", String.valueOf(parts.size()));

                    //set adapter to listview
                    participants.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        //return participants;
    }

    public void goToCurrentTripMainPanel(View v){

    }
}