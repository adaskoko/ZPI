package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());

        if (user != null){
            Log.i("user", user.toString());
            Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
        }

    }

    public void login(View view) {
        EditText mail = (EditText) findViewById(R.id.et_login);
        EditText password = (EditText) findViewById(R.id.et_password);

        new Thread(() -> {
            try {
                User user = new UserDao(BaseConnection.getConnectionSource()).findByEmail(mail.getText().toString());
                if(user == null){
                    Log.i("logowanko", "User nie istnieje");
                }else{
                    if(user.getPassword().equals(password.getText().toString())){
                        Log.i("logowanko", "Hasło gitara");
                        Toast.makeText(this, "Gitara", Toast.LENGTH_SHORT);
                        SharedPreferencesHandler.saveLoggedInUser(getApplicationContext(), user);
                        Intent intent = new Intent(this, UpdateUserActivity.class);
                        startActivity(intent);
                    }else{
                        Log.i("logowanko", "Hasło nie gitara");
                        Toast.makeText(this, "Nie gitara", Toast.LENGTH_SHORT);
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }


    public void forgotPassword(View view) {
    }

    public void register(View view) {
        Intent intent=new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}