package com.example.zpi.repositories;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.repositories_interfaces.IInvoiceDao;
import com.example.zpi.repositories_interfaces.ITripPointDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class InvoiceDao extends BaseDaoImpl<Invoice, Integer> implements IInvoiceDao {
    public InvoiceDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Invoice.class);
    }

    @Override
    public List<Invoice> getInvoicesFromTrip(Trip trip) throws SQLException {
        return super.queryForEq("TripID", trip.getID());
    }
}
