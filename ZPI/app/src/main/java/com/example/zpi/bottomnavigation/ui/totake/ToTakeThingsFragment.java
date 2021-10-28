package com.example.zpi.bottomnavigation.ui.totake;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentToTakeThingsBinding;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories.ProductToTakeDao;
import com.example.zpi.repositories.TripDao;

import java.sql.SQLException;
import java.util.List;


public class ToTakeThingsFragment extends Fragment implements ToTakeThingRecyclerViewAdapter.ToTakeThingListener {

    private ToTakeThingsViewModel toTakeThingsViewModel;
    private FragmentToTakeThingsBinding binding;
    private ToTakeThingRecyclerViewAdapter toTakeThingRecyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //toTakeThingsViewModel = new ViewModelProvider(this).get(ToTakeThingsViewModel.class);

        binding = FragmentToTakeThingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.btnAddToTake.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.action_navigation_to_take_things_to_addToTakeThingFragment));

        RecyclerView toTakeThingsRV = binding.recyclerViewToTakeThings;
        toTakeThingsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        toTakeThingsRV.setHasFixedSize(true);
        new ItemTouchHelper(itemTouchHelperCallbck).attachToRecyclerView(toTakeThingsRV);

        new Thread(() -> {
            try {
                Trip trip = new TripDao(BaseConnection.getConnectionSource()).queryForEq("ID", 1).get(0);
                List<ProductToTake> products = new ProductToTakeDao(BaseConnection.getConnectionSource()).getProductsByTrip(trip);

                Log.i("to take size fragment", String.valueOf(products.size()));
                getActivity().runOnUiThread(() -> {
                    toTakeThingRecyclerViewAdapter = new ToTakeThingRecyclerViewAdapter(products, this);
                    toTakeThingsRV.setAdapter(toTakeThingRecyclerViewAdapter);
                });
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallbck = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            toTakeThingRecyclerViewAdapter.deleteToTakeThingPosition(viewHolder.getAbsoluteAdapterPosition());
        }
    };

    @Override
    public void toTakeThingClick(int position) {
        Log.i("toTake click ", "clicked:" + position);
    }
}