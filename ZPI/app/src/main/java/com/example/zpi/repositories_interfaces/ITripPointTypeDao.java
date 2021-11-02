package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.TripPointType;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public interface ITripPointTypeDao extends Dao<TripPointType, Integer> {
    public TripPointType getTripPointTypeByName(String name) throws SQLException;
    public TripPointType getAtrakcjaTripPointType() throws SQLException;
    public TripPointType getNoclegTripPointType() throws SQLException;
}
