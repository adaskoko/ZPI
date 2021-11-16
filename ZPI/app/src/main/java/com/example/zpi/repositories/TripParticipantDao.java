package com.example.zpi.repositories;

import android.util.Log;

import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.ITripParticipantDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class TripParticipantDao extends BaseDaoImpl<TripParticipant, Integer> implements ITripParticipantDao {

    public TripParticipantDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, TripParticipant.class);
    }

    @Override
    public List<TripParticipant> getByUser(User user) throws SQLException {
        return super.queryForEq("UserID", user.getID());
    }

    @Override
    public List<TripParticipant> getByTrip(Trip trip) throws SQLException {
        List<TripParticipant> tripParticipants = super.queryForEq("TripID", trip.getID());

        UserDao dao = new UserDao(connectionSource);

        for(TripParticipant participant : tripParticipants){
            dao.refresh(participant.getUser());
        }

        return tripParticipants;
    }


}
