package com.example.zpi.bottomnavigation.ui.trip;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.zpi.PhotoGalleryActivity;
import com.example.zpi.R;
import com.example.zpi.models.Trip;

import static com.example.zpi.bottomnavigation.BottomNavigationActivity.TRIP_KEY;

public class TripMainpanelFragment extends Fragment {

    private TripMainpanelViewModel mViewModel;

    public static TripMainpanelFragment newInstance() {
        return new TripMainpanelFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.trip_mainpanel_fragment, container, false);
        Button more = (Button) view.findViewById(R.id.btn_more);
        more.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), PhotoGalleryActivity.class);
                Intent intent2 = getActivity().getIntent();
                Trip trip = (Trip) intent2.getSerializableExtra(TRIP_KEY);
                intent.putExtra(TRIP_KEY, trip);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TripMainpanelViewModel.class);
        // TODO: Use the ViewModel
    }

}