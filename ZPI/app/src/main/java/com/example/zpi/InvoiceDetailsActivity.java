package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zpi.adapters.InvoiceAdapter;
import com.example.zpi.adapters.UserCheckboxAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.Debtor;
import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories.DebtorDao;
import com.example.zpi.repositories.InvoiceDao;
import com.example.zpi.repositories.UserDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailsActivity extends AppCompatActivity {

    List<User> chosenUsers;
    User loggedInUser;
    Trip trip;
    ProgressDialog progressDialog;
    Invoice invoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_details);

        Intent intent = getIntent();
        invoice = (Invoice) intent.getSerializableExtra("INVOICE");
        trip = (Trip) intent.getSerializableExtra("TRIP");

        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Wczytywanie danych...");
        progressDialog.show();

        if (invoice == null){
            new Thread(() -> {
                try {
                    List<User> users = new UserDao(BaseConnection.getConnectionSource()).getUsersFromTrip(trip);
                    loggedInUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
                    users.removeIf(user -> user.getID() == loggedInUser.getID());

                    chosenUsers = new ArrayList<>();

                    runOnUiThread(() -> {
                        RecyclerView userRV = findViewById(R.id.rv_users);
                        UserCheckboxAdapter adapter = new UserCheckboxAdapter(users, null);

                        adapter.setOnItemClickListener(new UserCheckboxAdapter.ClickListener() {
                            @Override
                            public void onItemClick(int position, View v) {
                                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
                                checkBox.setChecked(!checkBox.isChecked());
                                if(checkBox.isChecked()){
                                    chosenUsers.add(users.get(position));
                                } else {
                                    chosenUsers.remove(users.get(position));
                                }
                                Log.i("size", String.valueOf(chosenUsers.size()));
                            }
                        });
                        userRV.setAdapter(adapter);
                        LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        userRV.setLayoutManager(upcomingLayoutManager);
                    });

                    progressDialog.dismiss();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    progressDialog.dismiss();
                }
            }).start();

        } else {
            new Thread(() -> {
                loggedInUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
                if(invoice.getUser().getID() == loggedInUser.getID()){
                    edit();
                } else {
                    show();
                }
            }).start();
        }
    }

    public void show(){
        runOnUiThread(() -> {
            ((Button) findViewById(R.id.save_button)).setVisibility(View.GONE);

            EditText nameET = (EditText) findViewById(R.id.et_name);
            nameET.setText(invoice.getDescription());
            nameET.setKeyListener(null);

            EditText priceET = (EditText) findViewById(R.id.et_price);
            priceET.setText(String.valueOf(invoice.getPrice()));
            priceET.setKeyListener(null);
        });

        try {
            List<Debtor> debtors = new DebtorDao(BaseConnection.getConnectionSource()).getDebtorsForInvoice(invoice);
            List<User> users = new ArrayList<>();
            for (Debtor debtor : debtors){
                users.add(debtor.getUser());
            }

            runOnUiThread(() -> {
                RecyclerView userRV = findViewById(R.id.rv_users);
                UserCheckboxAdapter adapter = new UserCheckboxAdapter(users, users);

                adapter.setOnItemClickListener(new UserCheckboxAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {

                    }
                });

                userRV.setAdapter(adapter);
                LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                userRV.setLayoutManager(upcomingLayoutManager);
            });

            progressDialog.dismiss();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            progressDialog.dismiss();
        }
    }

    public void edit(){
        runOnUiThread(() -> {
            EditText nameET = (EditText) findViewById(R.id.et_name);
            nameET.setText(invoice.getDescription());

            EditText priceET = (EditText) findViewById(R.id.et_price);
            priceET.setText(String.valueOf(invoice.getPrice()));
        });

        try {
            List<User> users = new UserDao(BaseConnection.getConnectionSource()).getUsersFromTrip(trip);
            loggedInUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
            users.removeIf(user -> user.getID() == loggedInUser.getID());

            chosenUsers = new ArrayList<>();
            List<Debtor> debtors = new DebtorDao(BaseConnection.getConnectionSource()).getDebtorsForInvoice(invoice);
            List<User> debtUsers = new ArrayList<>();
            for (Debtor debtor : debtors){
                debtUsers.add(debtor.getUser());
                chosenUsers.add(debtor.getUser());
            }

            runOnUiThread(() -> {
                RecyclerView userRV = findViewById(R.id.rv_users);
                UserCheckboxAdapter adapter = new UserCheckboxAdapter(users, debtUsers);

                adapter.setOnItemClickListener(new UserCheckboxAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
                        checkBox.setChecked(!checkBox.isChecked());
                        if(checkBox.isChecked()){
                            chosenUsers.add(users.get(position));
                        } else {
                            chosenUsers.removeIf(user -> user.getID() == users.get(position).getID());
                        }
                        Log.i("size", String.valueOf(chosenUsers.size()));
                    }
                });

                userRV.setAdapter(adapter);
                LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                userRV.setLayoutManager(upcomingLayoutManager);
            });

            progressDialog.dismiss();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            progressDialog.dismiss();
        }
    }

    public void save(View view) {
        if(chosenUsers.size() == 0){
            Toast.makeText(this, "Wybierz użytkowników!", Toast.LENGTH_SHORT).show();
        }else {

            double price = Double.parseDouble(((TextView) findViewById(R.id.et_price)).getText().toString());
            String name = ((TextView) findViewById(R.id.et_name)).getText().toString();

            progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            progressDialog.setTitle("Zapisywanie...");
            progressDialog.show();

            new Thread(() -> {
                if (invoice == null) {
                    addNewInvoice(price, name);
                } else {
                    editInvoice(price, name);
                }
            }).start();
        }
    }

    public void addNewInvoice(double price, String name){
        Invoice inv = new Invoice(price, name, loggedInUser, trip);
        try {
            new InvoiceDao(BaseConnection.getConnectionSource()).create(inv);

            DebtorDao debtorDao = new DebtorDao(BaseConnection.getConnectionSource());
            for(User user : chosenUsers){
                Debtor debtor = new Debtor(user, inv);
                debtorDao.create(debtor);
            }

            progressDialog.dismiss();
            finish();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            progressDialog.dismiss();
        }
    }

    public void editInvoice(double price, String name){
        try {
            DebtorDao debtorDao = new DebtorDao(BaseConnection.getConnectionSource());
            List<Debtor> debtors = debtorDao.getDebtorsForInvoice(invoice);

            for(User user : chosenUsers){
                boolean newDebtor = true;
                for(Debtor debtor : debtors){
                    if (user.getID() == debtor.getUser().getID()) {
                        Log.i("user user.getID()", String.valueOf(user.getID()));
                        newDebtor = false;
                        break;
                    }
                }
                Log.i("newDebtor", String.valueOf(newDebtor));
                if(newDebtor){
                    Debtor debtor = new Debtor(user, invoice);
                    debtorDao.create(debtor);
                }
            }

            for(Debtor debtor : debtors){
                boolean deleteDebtor = true;
                for(User user : chosenUsers){
                    if (user.getID() == debtor.getUser().getID()) {
                        Log.i("debtor user.getID()", String.valueOf(user.getID()));
                        deleteDebtor = false;
                        break;
                    }
                }
                Log.i("deleteDebtor", String.valueOf(deleteDebtor));
                if(deleteDebtor){
                    debtorDao.delete(debtor);
                }
            }

            invoice.setDescription(name);
            invoice.setPrice(price);
            new InvoiceDao(BaseConnection.getConnectionSource()).update(invoice);

            progressDialog.dismiss();
            finish();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            progressDialog.dismiss();
        }
    }
}