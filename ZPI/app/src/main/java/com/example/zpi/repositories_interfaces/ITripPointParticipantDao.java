package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointParticipant;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public interface ITripPointParticipantDao extends Dao<TripPointParticipant, Integer> {
    public List<TripPointParticipant> getParticipantsByUser(User user) throws SQLException;
    public List<TripPointParticipant> getParticipantsByTripPoint(TripPoint tripPoint) throws SQLException;
}
