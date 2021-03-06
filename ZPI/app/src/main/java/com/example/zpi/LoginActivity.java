package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;


public class LoginActivity extends AppCompatActivity {

    EditText mail;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hideSoftKeyboard();

        mail = findViewById(R.id.et_login);
        password = findViewById(R.id.et_pass);
        getWindow().setBackgroundDrawableResource(R.drawable.ic_tlo_cale);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());

        if (user != null){
            mail.setText(user.getEmail(), TextView.BufferType.EDITABLE);
            password.setText(user.getPassword(), TextView.BufferType.EDITABLE);
        }

    }

    public void login(View view) {
        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Logowanie...");
        progressDialog.show();

        new Thread(() -> {
            try {
                User user = new UserDao(BaseConnection.getConnectionSource()).findByEmail(mail.getText().toString());
                if(user == null){
                    Log.i("logowanko", "User nie istnieje");
                }else{
                    if(user.getPassword().equals(password.getText().toString())){
                        Log.i("logowanko", "Hasło gitara");
                        SharedPreferencesHandler.saveLoggedInUser(getApplicationContext(), user);
                        //Intent intent = new Intent(this, UpdateUserActivity.class);
                        Intent intent = new Intent(this, TripListActivity.class);
                        startActivity(intent);
                    }else{
                        Log.i("logowanko", "Hasło nie gitara");
                    }
                }
                progressDialog.dismiss();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                progressDialog.dismiss();
            }
        }).start();
    }


    public void forgotPassword(View view) {
    }

    public void register(View view) {
        Intent intent=new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}