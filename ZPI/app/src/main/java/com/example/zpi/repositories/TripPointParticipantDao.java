package com.example.zpi.repositories;

import android.util.Log;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.IInvoiceDao;
import com.example.zpi.repositories_interfaces.ITripPointParticipantDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class TripPointParticipantDao extends BaseDaoImpl<TripPointParticipant, Integer> implements ITripPointParticipantDao {
    public TripPointParticipantDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, TripPointParticipant.class);
    }

    @Override
    public List<TripPointParticipant> getParticipantsByUser(User user) throws SQLException {
        return super.queryForEq("UserID", user.getID());
    }

    @Override
    public List<TripPointParticipant> getParticipantsByTripPoint(TripPoint tripPoint) throws SQLException {
        return super.queryForEq("TripPointID", tripPoint.getID());
    }
}
