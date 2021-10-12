package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;

public class UpdateUserActivity extends AppCompatActivity {

    EditText name;
    EditText surname;
    EditText mail;
    User loggedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        name = findViewById(R.id.nameET);
        surname = findViewById(R.id.surnameET);
        mail = findViewById(R.id.mailET);
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());

        name.setText(loggedUser.getName(), TextView.BufferType.EDITABLE);
        surname.setText(loggedUser.getSurname(), TextView.BufferType.EDITABLE);
        mail.setText(loggedUser.getEmail(), TextView.BufferType.EDITABLE);
    }

    public void update(View view) {
        loggedUser.setName(name.getText().toString());
        loggedUser.setSurname(surname.getText().toString());
        SharedPreferencesHandler.saveLoggedInUser(getApplicationContext(), loggedUser);
        new Thread(() -> {
            try {
                UserDao userDao = new UserDao(BaseConnection.getConnectionSource());
                userDao.update(loggedUser);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }
}