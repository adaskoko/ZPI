package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        EditText name = (EditText) findViewById(R.id.nameET);
        EditText surname = (EditText) findViewById(R.id.surnameET);
        EditText email = (EditText) findViewById(R.id.emailET);
        EditText password = (EditText) findViewById(R.id.passwordET);
        EditText renamePassword = (EditText) findViewById(R.id.renamePasswordET);

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
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }).start();
        }
    }
}