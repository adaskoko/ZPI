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

public interface ITripPointDao extends Dao<TripPoint, Integer> {
    public void createTripPoint(String name, Date arrivalDate, Date departureDate, String remarks, Trip trip, TripPointLocation location, TripPointType type) throws SQLException;

    public void addInvoiceToTripPoint(Invoice invoice, TripPoint tripPoint) throws SQLException;
    public void addUserToTripPoint(User user, TripPoint tripPoint, double charge) throws SQLException;
    public List<TripPoint> getTripPointsByUser(User user) throws SQLException;
}
