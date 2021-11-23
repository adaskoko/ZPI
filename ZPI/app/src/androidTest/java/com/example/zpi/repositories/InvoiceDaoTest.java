package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class InvoiceDaoTest {

    @Test
    public void getInvoicesFromTrip() {

        try {
            Trip trip = new TripDao(BaseConnection.getConnectionSource()).queryForId(27);

            List<Invoice> invoices = new InvoiceDao(BaseConnection.getConnectionSource()).getInvoicesFromTrip(trip);
            Assert.assertEquals(1, invoices.size());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}