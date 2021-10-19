package com.example.zpi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;

public class ChangeUserPasswordActivity extends AppCompatActivity {

    User loggedUser;
    EditText oldPassword;
    EditText newPassword;
    EditText newPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        oldPassword = findViewById(R.id.et_oldPass);
        newPassword = findViewById(R.id.et_newPass);
        newPassword2 = findViewById(R.id.et_newPass2);

    }

    public void updatePassword(View v){
        String oldPass=oldPassword.getText().toString();
        String newPass=newPassword.getText().toString();
        String newPass2=newPassword2.getText().toString();
        if(loggedUser.getPassword().equals(oldPass)){
            if(newPass.equals(newPass2)){
                loggedUser.setPassword(newPass);
                new Thread(() -> {
                    try {
                        UserDao userDao = new UserDao(BaseConnection.getConnectionSource());
                        userDao.update(loggedUser);
                        SharedPreferencesHandler.saveLoggedInUser(getApplicationContext(), loggedUser);

                        //finish();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }).start();
                Intent intent = new Intent(this, ChangePasswordSuccessfulActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Podane hasła się różnią, spróbuj ponownie", Toast.LENGTH_LONG).show();
                newPassword.getText().clear();
                newPassword2.getText().clear();
            }
        }else{
            Toast.makeText(this,"Hasło nieprawidłowe",Toast.LENGTH_SHORT).show();
            oldPassword.getText().clear();
            newPassword.getText().clear();
            newPassword2.getText().clear();
        }
    }

    public void backToDetails(View v){
        Intent intent=new Intent(this, UpdateUserActivity.class);
        startActivity(intent);
    }
}
