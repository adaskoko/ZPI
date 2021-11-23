package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.zpi.adapters.InvoiceAdapter;
import com.example.zpi.adapters.PhotoInTripAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.InvoiceDao;

import java.sql.SQLException;
import java.util.List;

public class InvoicesActivity extends AppCompatActivity {

    public static final String TRIP_KEY = "TRIP";
    private Trip trip;
    List<Invoice> invoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);

        Intent intent = getIntent();
        trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Wczytywanie rachunków...");
        progressDialog.show();

        new Thread(() -> {
            try {
                invoices = new InvoiceDao(BaseConnection.getConnectionSource()).getInvoicesFromTrip(trip);

                runOnUiThread(() -> {
                    RecyclerView invoicesRV = findViewById(R.id.rv_invoices);
                    InvoiceAdapter invoiceAdapter = new InvoiceAdapter(invoices);

                    invoiceAdapter.setOnItemClickListener(new InvoiceAdapter.ClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            showInvoice(position);
                        }
                    });
                    invoicesRV.setAdapter(invoiceAdapter);
                    LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    invoicesRV.setLayoutManager(upcomingLayoutManager);
                });

                progressDialog.dismiss();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                progressDialog.dismiss();

            }
        }).start();

    }

    private void showInvoice(int position) {
        Intent intent = new Intent(this, InvoiceDetailsActivity.class);
        intent.putExtra("INVOICE", invoices.get(position));
        intent.putExtra("TRIP", trip);
        startActivity(intent);
    }

    public void addInvoice(View view) {
        Intent intent = new Intent(this, InvoiceDetailsActivity.class);
        intent.putExtra("TRIP", trip);
        startActivity(intent);
    }
}