package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;

public class ChangeUserNameActivity extends AppCompatActivity {

    User loggedUser;
    EditText newNameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        newNameET = findViewById(R.id.et_newName);
    }

    public void updateName(View v){
        String newName = newNameET.getText().toString();
        if (!newName.isEmpty()) {
            loggedUser.setName(newName);
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
            Toast.makeText(this, "Podane imie jest puste", Toast.LENGTH_LONG).show();
            newNameET.getText().clear();
        }
    }

}