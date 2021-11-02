package com.example.zpi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zpi.databinding.FragmentSingleTripBinding;
import com.example.zpi.models.Trip;

public class SingleTripFragment extends Fragment {

    public static final String TRIP_KEY="TRIP_KEY";
    FragmentSingleTripBinding binding;
    private Trip currTrip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentSingleTripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.btnParticipants.setOnClickListener(c->showParticipants());
        binding.btnForum.setOnClickListener(c->showForum());

        Intent intent = getActivity().getIntent();
        currTrip = (Trip) intent.getSerializableExtra("TRIP");

        return root;
    }

    public void showParticipants(){
        Bundle bundle=new Bundle();
        bundle.putSerializable(TRIP_KEY, currTrip);
        Navigation.findNavController(getView()).navigate(R.id.action_singleTripFragment_to_tripParticipantsFragment, bundle);
    }

    public void showForum(){

    }
}