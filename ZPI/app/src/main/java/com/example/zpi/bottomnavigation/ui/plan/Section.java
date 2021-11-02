package com.example.zpi.bottomnavigation.ui.plan;

import com.example.zpi.models.TripPoint;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private String title;
    private List<TripPoint> pointList;

    public Section(String title, List<TripPoint> pointList) {
        this.title = title;
        this.pointList = pointList;
    }

    public <K, V> Section(K k, ArrayList<V> vs) {
        this.title = (String) k;
        this.pointList = (List<TripPoint>) vs;
    }

    public String getTitle() {
        return title;
    }

    public List<TripPoint> getPointList() {
        return pointList;
    }
}
