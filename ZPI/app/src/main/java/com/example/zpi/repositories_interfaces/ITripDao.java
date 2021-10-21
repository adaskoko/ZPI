package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.Role;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface ITripDao extends Dao<Trip, Integer> {
    public void createTrip(String name, String description, Date startDate, Date endDate, User user) throws SQLException;
    public void addUserToTrip(Trip trip, User user, Role role) throws SQLException;

    public List<Trip> getTripsForUser(User user) throws SQLException;
    public Trip getCurrentTripForUser(User user) throws SQLException;
    public List<Trip> getPastTripsForUser(User user) throws SQLException;
    public List<Trip> getFutureTripsForUser(User user) throws SQLException;
}
