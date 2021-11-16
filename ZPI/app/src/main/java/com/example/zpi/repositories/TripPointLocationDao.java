package com.example.zpi.repositories;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointType;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.ITripPointDao;
import com.example.zpi.repositories_interfaces.ITripPointLocationDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class TripPointLocationDao extends BaseDaoImpl<TripPointLocation, Integer> implements ITripPointLocationDao {
    public TripPointLocationDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, TripPointLocation.class);
    }

    @Override
    public boolean tripPointLocationExists(TripPointLocation location) throws SQLException {
        return false;
    }

    @Override
    public TripPointLocation getLocationForTripPoint(TripPoint tripPoint) throws SQLException {
        super.refresh(tripPoint.getTripPointLocation());
        return tripPoint.getTripPointLocation();
    }
}
