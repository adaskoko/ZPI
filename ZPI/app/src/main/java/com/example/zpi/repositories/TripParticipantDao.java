package com.example.zpi.repositories;

import android.util.Log;

import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.ITripParticipantDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class TripParticipantDao extends BaseDaoImpl<TripParticipant, Integer> implements ITripParticipantDao {

    protected TripParticipantDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, TripParticipant.class);
    }

    @Override
    public List<TripParticipant> getByUser(User user) throws SQLException {
        Log.i("userId", String.valueOf(user.getID()));
        return super.queryForEq("UserID", user.getID());
    }

    @Override
    public List<TripParticipant> getByTrip(Trip trip) throws SQLException {
        return super.queryForEq("TripID", trip.getID());
    }


}