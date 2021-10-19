package com.example.zpi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordSuccessfulActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_sucessful);
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, UpdateUserActivity.class);
        startActivity(intent);
    }
}
