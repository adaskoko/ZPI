package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View view) {
        EditText name = (EditText) findViewById(R.id.et_name);
        EditText surname = (EditText) findViewById(R.id.et_surname);
        EditText email = (EditText) findViewById(R.id.et_email);
        EditText password = (EditText) findViewById(R.id.et_password);
        EditText renamePassword = (EditText) findViewById(R.id.et_password2);

        if (!password.getText().toString().equals(renamePassword.getText().toString())) {
            Toast.makeText(this, "Hasla sa rozne", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(() -> {
                try {
                    UserDao userDao = new UserDao(BaseConnection.getConnectionSource());
                    User user = userDao.findByEmail(email.getText().toString());
                    if(user != null){
                        Log.i("rejestracja", "User juz istnieje");
                    }else{
                        Log.i("rejestreacja", "Uzytkownik dodany");
                        user = new User(name.getText().toString(), surname.getText().toString(), email.getText().toString(), password.getText().toString());
                        userDao.create(user);
                        Intent intent = new Intent(this, RegisterSuccesfulActivity.class);
                        startActivity(intent);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }).start();
        }
    }
}