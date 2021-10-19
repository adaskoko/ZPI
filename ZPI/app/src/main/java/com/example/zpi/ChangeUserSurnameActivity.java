package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;

public class ChangeUserSurnameActivity extends AppCompatActivity {

    User loggedUser;
    EditText newSurnameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_surname);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        newSurnameET = findViewById(R.id.et_newSurname);
    }

    public void updateSurname(View v){
        String newSurname = newSurnameET.getText().toString();
        if (!newSurname.isEmpty()) {
            loggedUser.setSurname(newSurname);
            new Thread(() -> {
                try {
                    UserDao userDao = new UserDao(BaseConnection.getConnectionSource());
                    userDao.update(loggedUser);
                    SharedPreferencesHandler.saveLoggedInUser(getApplicationContext(), loggedUser);

                    finish();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }).start();
        } else {
            Toast.makeText(this, "Podane nazwisko jest puste", Toast.LENGTH_LONG).show();
            newSurnameET.getText().clear();
        }
    }

    public void backToDetails(View v){
        Intent intent=new Intent(this, UpdateUserActivity.class);
        startActivity(intent);
    }
}