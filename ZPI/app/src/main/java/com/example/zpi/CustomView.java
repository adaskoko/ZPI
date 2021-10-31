package com.example.zpi;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zpi.models.Trip;

public class CustomView extends RelativeLayout {

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView tripName;
    private TextView tripDate;

    private void init(){
        inflate(getContext(), R.layout.component_content, this);
        tripName = findViewById(R.id.tv_your_trips);
        tripDate = findViewById(R.id.tv_tripdate);
    }

    public void fill(Trip trip){
        tripName.setText(trip.getName());
        String range = trip.getStartDate().toString()+" - "+trip.getEndDate().toString();
        tripDate.setText(range);
    }
}