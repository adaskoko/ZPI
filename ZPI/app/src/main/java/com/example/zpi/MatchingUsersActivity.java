package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MatchingUsersActivity extends AppCompatActivity {

    ListView users;
    ArrayAdapter<String> adapter ;
    Trip currentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_users);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentTrip=(Trip)getIntent().getSerializableExtra("TRIP");
        users=findViewById(R.id.lv_results);
        String[] input= (String[])getIntent().getSerializableExtra("MATCH");
        String name=input[0];
        String surname=input[1];
        getMatchingUsers(name, surname);
    }

    private void getMatchingUsers(String name, String surname){
        List<String> returnList=new ArrayList<>();
        new Thread(() -> {
            try {
                UserDao userDao=new UserDao(BaseConnection.getConnectionSource());
                List <User> results=userDao.findByNameAndSurname(name, surname);
                if(results!=null) {
                    for (User u : results) {
                        String currentRow=u.getName()+" "+ u.getSurname()+" ("+u.getEmail()+")";
                        returnList.add(currentRow);
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            runOnUiThread(() -> updateList(returnList));

        }).start();
    }

    public void updateList(List<String> returnList){
        adapter = new ArrayAdapter<String>(this, R.layout.found_user_in_list, returnList);
        users.setAdapter(adapter);
        users.setOnItemClickListener((arg0, arg1, position, arg3) -> {
            String data=(String) arg0.getAdapter().getItem(position);
            String[] splitData=data.split("\\(");
            String email=splitData[1].substring(0, splitData[1].length()-1);

            new Thread(() -> {
                try {
                    UserDao userDao=new UserDao(BaseConnection.getConnectionSource());
                    User user=userDao.findByEmail(email);
                    if(user!=null){
                        TripDao tripDao=new TripDao(BaseConnection.getConnectionSource());
                        tripDao.addRegularParticipant(currentTrip, user);
                        finish();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }).start();
        });
    }

}