package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ITripParticipantDao extends Dao<TripParticipant, Integer> {
    public List<TripParticipant> getByUser(User user) throws SQLException;
    public List<TripParticipant> getByTrip(Trip trip) throws SQLException;
}
