package com.example.zpi.repositories;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPointType;
import com.example.zpi.repositories_interfaces.IInvoiceDao;
import com.example.zpi.repositories_interfaces.ITripPointTypeDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class TripPointTypeDao extends BaseDaoImpl<TripPointType, Integer> implements ITripPointTypeDao {
    public TripPointTypeDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, TripPointType.class);
    }

    @Override
    public TripPointType getTripPointTypeByName(String name) throws SQLException {
        List<TripPointType> types = this.queryForEq("Name", name);
        if (types.size() == 1)
            return types.get(0);
        return null;
    }

    @Override
    public TripPointType getAtrakcjaTripPointType() throws SQLException {
        return this.queryForEq("ID", 1).get(0);
    }

    @Override
    public TripPointType getNoclegTripPointType() throws SQLException {
        return this.queryForEq("ID", 2).get(0);
    }
}
