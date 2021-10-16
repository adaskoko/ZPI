package com.example.zpi;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;

public class UserDetailsActivity extends AppCompatActivity {

    EditText name;
    EditText surname;
    EditText mail;
    EditText password;
    User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_update_user_1);
    }

    @Override
    protected void onStart() {
        super.onStart();loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        mail = findViewById(R.id.email);
        password=findViewById(R.id.password);


        name.setText(loggedUser.getName(), TextView.BufferType.EDITABLE);
        surname.setText(loggedUser.getSurname(), TextView.BufferType.EDITABLE);
        mail.setText(loggedUser.getEmail(), TextView.BufferType.EDITABLE);
        password.setText(loggedUser.getPassword(), TextView.BufferType.EDITABLE);
    }

    //
}
