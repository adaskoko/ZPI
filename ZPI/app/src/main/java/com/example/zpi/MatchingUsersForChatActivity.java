package com.example.zpi;

import static com.example.zpi.ChatListActivity.CHAT_KEY;
import static com.example.zpi.SearchUserForNewConversationActivity.NEW_CHAT_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MatchingUsersForChatActivity extends AppCompatActivity {

    ListView users;
    ArrayAdapter<String> adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_users_for_chat);
    }

    @Override
    protected void onStart() {
        super.onStart();
        users=findViewById(R.id.lvUsers);
        String[] input= (String[])getIntent().getSerializableExtra(NEW_CHAT_KEY);
        String name=input[0];
        String surname=input[1];

        getMatchingUsersForChat(name, surname);
    }

    private void getMatchingUsersForChat(String name, String surname){
        List<String> returnList=new ArrayList<>();
        new Thread(() -> {
            try {
                UserDao userDao=new UserDao(BaseConnection.getConnectionSource());
                List <User> results=userDao.findByNameAndSurname(name, surname);
                if(results!=null) {
                    for (User u : results) {
                        String currentRow=u.getName()+" "+ u.getSurname()+"("+u.getEmail()+")";
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
        //set adapter to listview
        users.setAdapter(adapter);

        users.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0,View arg1, int position, long arg3)
            {
                String data=(String) arg0.getAdapter().getItem(position);
                String[] splitData=data.split("\\(");
                String email=splitData[1].substring(0, splitData[1].length()-1);

                new Thread(()->{
                    try{
                        UserDao userDao=new UserDao(BaseConnection.getConnectionSource());
                        User user=userDao.findByEmail(email);
                        Intent i=new Intent(MatchingUsersForChatActivity.this, ChatActivity.class);
                        i.putExtra(CHAT_KEY, user);
                        startActivity(i);
                    }catch(SQLException throwables){
                        throwables.printStackTrace();
                    }
                }).start();

            }
        });
    }

    public void finishMUFC(View v){
        super.finish();
    }
}