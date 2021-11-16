package com.example.zpi.bottomnavigation.ui.plan;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.R;
import com.example.zpi.bottomnavigation.ui.totake.ToTakeThingRecyclerViewAdapter;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentPlanBinding;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.repositories.TripPointDao;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.sql.SQLException;
import java.util.List;

public class PlanFragment extends Fragment implements PlanChildRecyclerViewAdapter.OnChildTripPointListener, AccommodationRecyclerViewAdapter.OnAccommodationListener {

    public final static String PLAN_KEY = "PLAN";
    private Trip currTrip;
    private FragmentPlanBinding binding;
    private PlanRecyclerViewAdapter planRecyclerViewAdapter;
    private List<Section> attractionPoints;
    private List<TripPoint> accommodationList;
    private List<TripPoint> points;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra(TRIP_KEY);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //planViewModel = new ViewModelProvider(this).get(PlanViewModel.class);
        binding = FragmentPlanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView planRV = binding.parentList;
        planRV.setLayoutManager(new LinearLayoutManager(getContext()));
        planRV.setHasFixedSize(true);

        RecyclerView accommodationRV = binding.accommodationList;
        planRV.setLayoutManager(new LinearLayoutManager(getContext()));
        planRV.setHasFixedSize(true);

        Thread databaseThread = new Thread(() -> {
            try {
                points = new TripPointDao(BaseConnection.getConnectionSource()).getTripPointsByTrip(currTrip);
                init();
                getActivity().runOnUiThread(() -> {
                    PlanRecyclerViewAdapter planRecyclerViewAdapter = new PlanRecyclerViewAdapter(attractionPoints, this);
                    planRV.setAdapter(planRecyclerViewAdapter);

                    AccommodationRecyclerViewAdapter accommodationRecyclerViewAdapter = new AccommodationRecyclerViewAdapter(accommodationList, this);
                    accommodationRV.setAdapter(accommodationRecyclerViewAdapter);
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        databaseThread.start();
        binding.btnAddAccommodation.setOnClickListener(c -> addAccommodation());
        binding.btnAddAttraction.setOnClickListener(c -> addAttraction());

        return root;
    }

    private void addAttraction() {
        NavHostFragment.findNavController(this).navigate(R.id.action_navigation_plan_to_addAttractionFragment);
    }

    private void addAccommodation() {
        NavHostFragment.findNavController(this).navigate(R.id.action_navigation_plan_to_addAccomodationFragment);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void init() {
        HashList<String, TripPoint> list = new HashList<>();
        accommodationList = new ArrayList<>();
        //Log.i("all plan size", String.valueOf(points.size()));
        for (TripPoint point : points) {
//            Log.i("plan", String.valueOf(points.size()));
//            Date date = point.getArrivalDate();
//            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//            list.put(dateFormat.format(date), point);
            //Log.i("trip point name", String.valueOf(point.getTripPointType().getName()));
            if (point.getTripPointType().getName().equals("Nocleg")) {
                accommodationList.add(point);
            } else {
                Date date = point.getArrivalDate();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                //Log.i("attraction date", dateFormat.format(date));
                list.put(dateFormat.format(date), point);
            }
        }
        attractionPoints = list.getSections();
        //Log.i("accommodation list size", String.valueOf(accommodationList.size()));
        //Log.i("section list size", String.valueOf(attractionPoints.size()));
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //planRecyclerViewAdapter.deleteTodoPosition(viewHolder.getAbsoluteAdapterPosition());
        }
    };

    @Override
    public void onChildClick(int id) {
        TripPoint point = null;
        Log.i("tripPoint click id", String.valueOf(id));
        for (TripPoint p : points) {
            Log.i("tripPoint list id", String.valueOf(p.getID()));
            if (p.getID() == id) {
                point = p;
            }
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(PLAN_KEY, point);
        Log.i("nazwa", point.getName());
        Log.i("typ",point.getTripPointType().getName());
        if (point.getTripPointType().getName().equals("Nocleg")) {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_plan_to_AccomodationDetailsFragment, bundle);
            Log.i("tripPoint click", "nocleg");
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_plan_to_AttractionDetailsFragment, bundle);
            Log.i("tripPoint click", "atraction");
        }
    }

    @Override
    public void onAccommodationClick(int position) {
        TripPoint accommodation = accommodationList.get(position);
        Log.i("accommodation click id", String.valueOf(position));

        Bundle bundle = new Bundle();
        bundle.putSerializable(PLAN_KEY, accommodation);
        NavHostFragment.findNavController(this).navigate(R.id.action_navigation_plan_to_AccomodationDetailsFragment, bundle);
    }
}