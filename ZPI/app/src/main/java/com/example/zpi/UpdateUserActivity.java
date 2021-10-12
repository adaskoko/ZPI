package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
        //loggedUser = SharedPreferencesHandler.getLoggedInUser(this);

        name.setText("imie", TextView.BufferType.EDITABLE);
        surname.setText("nazwisko", TextView.BufferType.EDITABLE);
        mail.setText("mail", TextView.BufferType.EDITABLE);
    }

    public void update(View view) {
        //User updatedUser = new User(name.getText().toString(), surname.getText().toString(), mail.getText().toString());
        loggedUser.setName(name.getText().toString());
        loggedUser.setSurname(surname.getText().toString());
        //SharedPreferencesHandler.saveLoggedInUser(updatedUser);
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