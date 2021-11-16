package com.example.zpi;

import static com.example.zpi.ChooseListForThreadActivity.A_PLAN_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChooseSpecificPointForThreadActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> points=new ArrayList<>();
    PointListAdapter pointListAdapter;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_specific_point_for_thread);
        recyclerView=findViewById(R.id.rv_points);

        key=(String)getIntent().getSerializableExtra(A_PLAN_KEY);
        displayPoints();
    }

    public void displayPoints(){
        switch (key) {
            case "Ogólne":
                points.add(key + " -ogólne");
                break;
            case "Plan przygotowań":
                getTodosNames();
                break;



        }
    }

    private void getTodosNames(){}
    private void getPointSNames(){}
    private void getToTakesNames(){}


    private class PointListAdapter extends RecyclerView.Adapter<PointListAdapter.PointListAdapterVh>{

        private List<String> options;
        private Context context;

        public PointListAdapter(List<String> list){
            this.options=list;
        }

        @NonNull
        @Override
        public PointListAdapter.PointListAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            context=parent.getContext();
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_for_plans, parent, false);
            return new PointListAdapterVh(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PointListAdapterVh holder, int position){
            String current=options.get(position);
            holder.tv_title.setText(current);
        }

        @Override
        public int getItemCount(){
            return options.size();
        }

        private class PointListAdapterVh extends RecyclerView.ViewHolder{
            TextView tv_title;

            public PointListAdapterVh(@NonNull View itemView){
                super(itemView);
                tv_title=itemView.findViewById(R.id.tv_title);
                itemView.setOnClickListener(v->{
                    Intent returnIntent2=new Intent();
                    returnIntent2.putExtra("result2", tv_title.getText());
                    setResult(Activity.RESULT_OK, returnIntent2);
                    finish();
                });
            }
        }
    }

}