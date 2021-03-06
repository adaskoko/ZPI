package com.example.zpi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.zpi.data_handling.SharedPreferencesHandler;
import com.example.zpi.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChooseListForThreadActivity extends AppCompatActivity {

    User loggedUser;
    RecyclerView recyclerView;
    private List<String> availablePlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_list_for_thread);
        loggedUser = SharedPreferencesHandler.getLoggedInUser(getApplicationContext());
        recyclerView=findViewById(R.id.rv_plans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        availablePlans=new ArrayList<>();
        displayList();
    }

    public void displayList(){
        availablePlans.add("Ogólne");
        availablePlans.add("Plan przygotowań");
        availablePlans.add("Plan wycieczki");
        availablePlans.add("Produkty do zabrania");
        availablePlans.add("Trasa");
        availablePlans.add("Zdjęcia");
        availablePlans.add("Rachunki");
        PlansListAdapter adapter=new PlansListAdapter(availablePlans);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class PlansListAdapter extends RecyclerView.Adapter<PlansListAdapter.PlansListAdapterVh> implements Filterable {

        private final List<String> plans;

        public PlansListAdapter(List<String> list){
            this.plans=list;
        }

        @Override
        public Filter getFilter() {
            return null;
        }

        @NonNull
        @Override
        public PlansListAdapter.PlansListAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_for_plans, parent, false);
            return new PlansListAdapterVh(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PlansListAdapter.PlansListAdapterVh holder, int position){
            String plan=plans.get(position);
            Log.i("MAM DOSC", String.valueOf(plan));
            holder.tv_title.setText(plan);
        }

        @Override
        public int getItemCount(){
            return plans.size();
        }


        private class PlansListAdapterVh extends RecyclerView.ViewHolder{
            TextView tv_title;

            public PlansListAdapterVh(@NonNull View itemView){
                super(itemView);
                tv_title=itemView.findViewById(R.id.tv_title);

                itemView.setOnClickListener(v->{
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",tv_title.getText());
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                });
            }
        }
    }
}