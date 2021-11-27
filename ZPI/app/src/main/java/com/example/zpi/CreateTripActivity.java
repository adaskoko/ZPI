package com.example.zpi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.TripDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText name;
    EditText description;
    EditText begin;
    EditText end;
    User loggedUser;
    private int iFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        findViewById(R.id.et_tripBegin).setOnClickListener(v -> {
            iFlag = 1;
            showDatePickerDialog();
        });
        findViewById(R.id.et_tripEnd).setOnClickListener(v -> {
            iFlag = 2;
            showDatePickerDialog();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        name = findViewById(R.id.et_tripName);
        description = findViewById(R.id.et_tripDesc);
        begin = findViewById(R.id.et_tripBegin);
        end = findViewById(R.id.et_tripEnd);
    }

    public void createTrip(View v) throws ParseException {
        String tripname = name.getText().toString();
        String tripdescription = description.getText().toString();
        String sTripBegin = begin.getText().toString();
        String sTripEnd = end.getText().toString();

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dTripBegin = formatter.parse(sTripBegin);
        Date dTripEnd = formatter.parse(sTripEnd);


        if(tripname!=null && tripdescription!=null && sTripBegin!=null && sTripEnd!=null){
            if(dTripBegin.after(new Date())) {
                if(dTripBegin.before(dTripEnd)) {
                    new Thread(() -> {
                        try {
                            TripDao tripDao = new TripDao(BaseConnection.getConnectionSource());
                            Trip currentTrip = new Trip(tripname, tripdescription, dTripBegin, dTripEnd);
                            tripDao.createTrip(currentTrip, loggedUser);
                            Intent intent = new Intent(this, InviteUsersActivity.class);
                            intent.putExtra("CreateTrip", currentTrip);
                            startActivity(intent);
                            //BaseConnection.closeConnection();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }).start();
                }else{
                    Toast.makeText(this, "Niezgodność dat!", Toast.LENGTH_SHORT).show();
                    end.getText().clear();
                }
            }else{
                Toast.makeText(this, "Zła data rozpoczęcia!", Toast.LENGTH_SHORT).show();
                begin.getText().clear();
                end.getText().clear();
            }
        }else{
            Toast.makeText(this, "Proszę uzupełnić wszystkie pola!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String date = year + "-" + month +"-"+ dayOfMonth;
        if (iFlag == 1) {
            begin.setText(date);
        }
        else if (iFlag == 2) {
            end.setText(date);

        }
    }
}