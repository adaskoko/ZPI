package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Debtor;
import com.example.zpi.models.Invoice;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class DebtorDaoTest {

    @Test
    public void getDebtorsForInvoice() {

        try {
            Invoice invoice = new InvoiceDao(BaseConnection.getConnectionSource()).queryForId(1);
            List<Debtor> debtors = new DebtorDao(BaseConnection.getConnectionSource()).getDebtorsForInvoice(invoice);

            Assert.assertEquals(1, debtors.size());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}