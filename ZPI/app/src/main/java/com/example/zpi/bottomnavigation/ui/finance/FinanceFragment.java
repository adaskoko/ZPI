package com.example.zpi.bottomnavigation.ui.finance;

import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.adapters.FinanceAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.databinding.FragmentFinanceBinding;
import com.example.zpi.models.Debt;
import com.example.zpi.models.Debtor;
import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.models.UserAmount;
import com.example.zpi.repositories.DebtorDao;
import com.example.zpi.repositories.InvoiceDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinanceFragment extends Fragment {

    private FinanceViewModel financeViewModel;
    private FragmentFinanceBinding binding;
    private Trip trip;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        financeViewModel = new ViewModelProvider(this).get(FinanceViewModel.class);

        binding = FragmentFinanceBinding.inflate(inflater, container, false);

        Intent intent = getActivity().getIntent();
        trip = (Trip) intent.getSerializableExtra("TRIP");

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        ProgressDialog progressDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Pobieranie danych...");
        getActivity().runOnUiThread(() -> progressDialog.show());

        new Thread(() -> {
            User loggedUser = SharedPreferencesHandler.getLoggedInUser(getActivity().getApplicationContext());

            List<UserAmount> paid = new ArrayList<>();
            List<UserAmount> owes = new ArrayList<>();

            List<Debt> debts = new ArrayList<>();

            try {
                List<Invoice> invoices = new InvoiceDao(BaseConnection.getConnectionSource()).getInvoicesFromTrip(trip);

                for(Invoice inv : invoices){
                    List<Debtor> debtors = new DebtorDao(BaseConnection.getConnectionSource()).getDebtorsForInvoice(inv);

                    Double pricePerUSer = inv.getPrice() / (debtors.size() + 1);

                    boolean payerExists = false;
                    for(UserAmount userAmount : paid){
                        if(userAmount.getUser().getID() == inv.getUser().getID()){
                            userAmount.addAmount(pricePerUSer * debtors.size());
                            payerExists = true;
                        }
                    }
                    if(!payerExists){
                        paid.add(new UserAmount(inv.getUser(), pricePerUSer * debtors.size()));
                    }

                    for(Debtor debtor : debtors){
                        boolean debtorExists = false;
                        for(UserAmount userAmount : owes){
                            if(userAmount.getUser().getID() == debtor.getUser().getID()){
                                userAmount.addAmount(pricePerUSer);
                                debtorExists = true;
                            }
                        }
                        if(!debtorExists){
                            owes.add(new UserAmount(debtor.getUser(), pricePerUSer));
                        }
                    }
                }

                for(UserAmount payer : paid){
                    for(UserAmount debtor : owes){
                        if(payer.getUser().getID() == debtor.getUser().getID()){
                            Double amount = Math.min(payer.getAmount(), debtor.getAmount());
                            payer.subtractAmount(amount);
                            debtor.subtractAmount(amount);
                            if(payer.getAmount() == 0){
                                paid.remove(payer);
                            }
                            if(debtor.getAmount() == 0){
                                owes.remove(debtor);
                            }
                        }
                    }
                }

                Collections.sort(paid);
                Collections.sort(owes);

                while(owes.size() > 0 && paid.size() > 0){
                    Log.i("owes size", String.valueOf(owes.size()));
                    UserAmount debtor = owes.get(0);
                    UserAmount payer = paid.get(0);

                    Double amount = Math.min(payer.getAmount(), debtor.getAmount());
                    payer.subtractAmount(amount);
                    debtor.subtractAmount(amount);
                    if(payer.getAmount() == 0){
                        paid.remove(payer);
                    }
                    if(debtor.getAmount() == 0){
                        owes.remove(debtor);
                    }

                    Collections.sort(paid);
                    Collections.sort(owes);

                    debts.add(new Debt(debtor.getUser(), payer.getUser(), amount));
                }

                Log.i("debts size", String.valueOf(debts.size()));

            } catch (SQLException throwables) {
                throwables.printStackTrace();
                progressDialog.dismiss();
            }

            getActivity().runOnUiThread(() -> {
                FinanceAdapter adapter = new FinanceAdapter(debts, loggedUser);

                adapter.setOnItemClickListener((position, v) -> {
                });

                LinearLayoutManager upcomingLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                binding.rvFinances.setLayoutManager(upcomingLayoutManager);
                binding.rvFinances.setAdapter(adapter);
            });

            progressDialog.dismiss();
        }).start();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}