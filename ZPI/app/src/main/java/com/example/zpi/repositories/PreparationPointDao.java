package com.example.zpi.repositories;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories_interfaces.IInvoiceDao;
import com.example.zpi.repositories_interfaces.IPreparationPointDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class PreparationPointDao extends BaseDaoImpl<PreparationPoint, Integer> implements IPreparationPointDao {
    public PreparationPointDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, PreparationPoint.class);
    }

    public List<PreparationPoint> getPreparationPointsByTrip(Trip trip) throws SQLException{
        return super.queryForEq("TripID", trip.getID());
    }

}
