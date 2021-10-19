package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;

public class UpdateUserActivity extends AppCompatActivity {

    EditText name;
    EditText surname;
    TextView mail;
    EditText password;
    User loggedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        mail = findViewById(R.id.email);
        password=findViewById(R.id.password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        name.setText(loggedUser.getName(), TextView.BufferType.EDITABLE);
        surname.setText(loggedUser.getSurname(), TextView.BufferType.EDITABLE);
        mail.setText(loggedUser.getEmail(), TextView.BufferType.EDITABLE);
        password.setText(loggedUser.getPassword(), TextView.BufferType.EDITABLE);
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

    public void editName(View v) {
        name.getText().clear();
        Intent intent = new Intent(this, ChangeUserNameActivity.class);
        startActivity(intent);
    }

    public void editSurname(View v) {
        surname.getText().clear();
        Intent intent = new Intent(this, ChangeUserSurnameActivity.class);
        startActivity(intent);
    }

    public void goToEditPassword(View v) {
        Intent intent = new Intent(this, ChangeUserPasswordActivity.class);
        startActivity(intent);
    }

    public void logout(View v) {
        SharedPreferencesHandler.deleteLoggedInUser(getApplicationContext());
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToChat(View v) {
        //chat not yet implemented
    }

    public void deleteAccount(View v) {
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        new Thread(() -> {
            try {
                UserDao userDao = new UserDao(BaseConnection.getConnectionSource());
                userDao.delete(loggedUser);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void back(View v) {
        //back activity (wycieczka?) not yet implemented
    }
}