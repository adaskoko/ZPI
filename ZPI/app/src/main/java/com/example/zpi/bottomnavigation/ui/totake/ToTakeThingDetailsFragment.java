package com.example.zpi.bottomnavigation.ui.totake;

import static com.example.zpi.bottomnavigation.ui.totake.ToTakeThingsFragment.TOTAKE_KEY;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.R;
import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.databinding.FragmentToTakeThingDetailsBinding;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.repositories.ProductToTakeDao;

import java.sql.SQLException;


public class ToTakeThingDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;

    private ProductToTake actPoint;
    FragmentToTakeThingDetailsBinding binding;

    public ToTakeThingDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //* @param param1 Parameter 1.
     //* @param param2 Parameter 2.
     * @return A new instance of fragment ToTakeThingDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    //public static ToTakeThingDetailsFragment newInstance(String param1, String param2) {
        //ToTakeThingDetailsFragment fragment = new ToTakeThingDetailsFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        //return fragment;
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actPoint = (ProductToTake) getArguments().get(TOTAKE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentToTakeThingDetailsBinding.inflate(inflater, container, false);
        fillTextViews();
        binding.btnDeteleToTake.setOnClickListener(c->delete());
        binding.btnEditToTake.setOnClickListener(c->edit());
        return binding.getRoot();
    }

    private void fillTextViews(){
        binding.productNameTV.setText(actPoint.getName());
        binding.tvPrPersonResponsible.setText(actPoint.getUser().getName());
        binding.cbDone.setChecked(actPoint.isDone());
    }

    private void delete(){
        new Thread(() -> {
            try {
                ProductToTakeDao pointDao = new ProductToTakeDao(BaseConnection.getConnectionSource());
                pointDao.delete(actPoint);
                Log.i("todo", "usunieto todo");

                //BaseConnection.closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
        NavHostFragment.findNavController(this).navigate(R.id.action_toTakeThingDetailsFragment_to_navigation_to_take_things);
    }

    private void edit(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(TOTAKE_KEY, actPoint);
        NavHostFragment.findNavController(this).navigate(R.id.action_toTakeThingDetailsFragment_to_toTakeThingsEditFragment, bundle);
    }
}