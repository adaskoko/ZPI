package com.example.zpi.bottomnavigation.ui.plan;

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

public class PlanFragment extends Fragment implements PlanChildRecyclerViewAdapter.OnChildTripPointListener {

    public final static String PLAN_KEY = "PLAN";
    private Trip currTrip;
    private PlanViewModel planViewModel;
    private FragmentPlanBinding binding;
    private PlanRecyclerViewAdapter planRecyclerViewAdapter;
    private List<Section> attractionPoints;
    private List<TripPoint> accommodationList;
    private List<TripPoint> points;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //planViewModel = new ViewModelProvider(this).get(PlanViewModel.class);
        binding = FragmentPlanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView planRV = binding.parentList;
        planRV.setLayoutManager(new LinearLayoutManager(getContext()));
        planRV.setHasFixedSize(true);

        Thread databaseThread = new Thread(() -> {
            try {
                points = new TripPointDao(BaseConnection.getConnectionSource()).getTripPointsByTrip(currTrip);
                init();
                Log.i("points size fragemnt", String.valueOf(points.size()));
                getActivity().runOnUiThread(() -> {
                    PlanRecyclerViewAdapter planRecyclerViewAdapter = new PlanRecyclerViewAdapter(attractionPoints, this);
                    planRV.setAdapter(planRecyclerViewAdapter);
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        databaseThread.start();
        binding.btnAddAccommodation.setOnClickListener(c -> addAccommodation());
        binding.btnAddAttraction.setOnClickListener(c -> addAttraction());

//        while (databaseThread.isAlive()) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        PlanRecyclerViewAdapter planRecyclerViewAdapter = new PlanRecyclerViewAdapter(attractionPoints, this);
//        planRV.setAdapter(planRecyclerViewAdapter);

//        planViewModel.getTripPointList().observe(getViewLifecycleOwner(), new Observer<List<TripPoint>>() {
//            @Override
//            public void onChanged(List<TripPoint> tripPoints) {
//                // update list
//                Toast.makeText(getContext(), "Updated list", Toast.LENGTH_SHORT).show();
//            }
//        });
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
        accommodationList = new ArrayList<TripPoint>();
       HashList<String, TripPoint> list = new HashList<>();
        accommodationList = new ArrayList<>();
        for (TripPoint point : points) {
//            Log.i("plan size", String.valueOf(points.size()));
//            Date date = point.getArrivalDate();
//            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//            list.put(dateFormat.format(date), point);
            Log.i("plan", String.valueOf(points.size()));
            if (point.getTripPointType().getName().equals("Nocleg")) {
                accommodationList.add(point);
            } else {
                Date date = point.getArrivalDate();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                list.put(dateFormat.format(date), point);
            }
        }
        attractionPoints = list.getSections();
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
        if (point.getTripPointType().equals("Nocleg")) {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_plan_to_AccomodationDetailsFragment, bundle);
            Log.i("tripPoint click", "nocleg");
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_plan_to_AttractionDetailsFragment, bundle);
            Log.i("tripPoint click", "atraction");
        }
    }
}