package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.User;
import com.example.zpi.repositories.UserDao;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*User user = new User();
        user.setEmail("test_dao@test.com");
        user.setBirthDate(new Date());
        user.setJoiningDate(new Date());
        user.setName("test name");
        user.setPassword("test password");
        user.setSurname("test surname");

        new Thread(() -> {
            try {
                new UserDao(BaseConnection.getConnectionSource()).create(user);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();*/
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToRegistry(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}