package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointType;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface ITripPointLocationDao extends Dao<TripPointLocation, Integer> {
    public boolean tripPointLocationExists(TripPointLocation location) throws SQLException; //TODO: nie zaimplementowane bo nie wiem czy potrzebne xd
}
