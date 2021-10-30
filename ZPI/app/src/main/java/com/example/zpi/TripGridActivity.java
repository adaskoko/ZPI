package com.example.zpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.zpi.adapters.TripAdapter;
import com.example.zpi.bottomnavigation.BottomNavigationActivity;
import com.example.zpi.models.Trip;

import java.util.List;

public class TripGridActivity extends AppCompatActivity {

    List<Trip> trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_grid);

        Intent intent = getIntent();

        TextView title = findViewById(R.id.tv_grid_title);
        title.setText(intent.getStringExtra("TITLE"));

        trips = (List<Trip>) intent.getSerializableExtra("TRIPS");

        RecyclerView recycler = (RecyclerView) findViewById(R.id.rv_trip_grid);

        TripAdapter upcomingAdapter = new TripAdapter(trips);
        upcomingAdapter.setOnItemClickListener(new TripAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
                intent.putExtra("TRIP", trips.get(position));
                startActivity(intent);
            }
        });
        recycler.setAdapter(upcomingAdapter);
        LinearLayoutManager upcomingLayoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(upcomingLayoutManager);
    }

    public void back(View view) {
        finish();
    }
}