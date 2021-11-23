package com.example.zpi.bottomnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.example.zpi.ChatListActivity;
import com.example.zpi.PhotoGalleryActivity;
import com.example.zpi.R;


import com.example.zpi.databinding.ActivityBottomNavigationBinding;
import com.example.zpi.models.Trip;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BottomNavigationActivity extends AppCompatActivity {

    private ActivityBottomNavigationBinding binding;
    public static final String TRIP_KEY = "TRIP";
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        trip = (Trip) intent.getSerializableExtra(TRIP_KEY);
//        Toast.makeText(this, trip.getName(), Toast.LENGTH_SHORT).show();

        binding = ActivityBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        binding.tvTripname.setText(trip.getName());
        String startDate = dateFormat.format(trip.getStartDate());
        String endDate = dateFormat.format(trip.getEndDate());
        startDate += " - ";
        startDate += endDate;
        binding.tvTripdate.setText(startDate);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_todo, R.id.navigation_to_take_things, R.id.navigation_plan, R.id.navigation_map, R.id.navigation_finance)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation);
        NavController navController = navHostFragment.getNavController();
        binding.tvTripname.setOnClickListener(v -> {
            navController.navigateUp();
            navController.navigate(R.id.singleTripFragment);
        });
        //navView.getMenu().getItem(0).setVisible(false);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void goToPhotos(View v){
        Intent intent = new Intent(this, PhotoGalleryActivity.class);
        intent.putExtra(TRIP_KEY, trip);
        startActivity(intent);
    }

    public void goToInvoices(View view) {
        Intent intent = new Intent(this, InvoicesActivity.class);
        intent.putExtra(TRIP_KEY, trip);
        startActivity(intent);
    }

    public void openChat(View v){
        Intent intent=new Intent(this, ChatListActivity.class);
        startActivity(intent);
    }

}