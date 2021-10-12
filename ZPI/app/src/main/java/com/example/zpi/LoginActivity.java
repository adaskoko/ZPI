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


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        EditText mail = (EditText) findViewById(R.id.mail);
        EditText password = (EditText) findViewById(R.id.password);

        new Thread(() -> {
            try {
                User user = new UserDao(BaseConnection.getConnectionSource()).findByEmail(mail.getText().toString());
                if(user == null){
                    Log.i("logowanko", "User nie istnieje");
                }else{
                    if(user.getPassword().equals(password.getText().toString())){
                        Log.i("logowanko", "Hasło gitara");
                    }else{
                        Log.i("logowanko", "Hasło nie gitara");
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }
}