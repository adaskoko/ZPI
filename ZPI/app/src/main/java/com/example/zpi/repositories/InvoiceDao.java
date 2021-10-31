package com.example.zpi.repositories;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.TripPoint;
import com.example.zpi.repositories_interfaces.IInvoiceDao;
import com.example.zpi.repositories_interfaces.ITripPointDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class InvoiceDao extends BaseDaoImpl<Invoice, Integer> implements IInvoiceDao {
    public InvoiceDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Invoice.class);
    }
}
