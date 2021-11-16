package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.Debtor;
import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public interface IDebtorDao extends Dao<Debtor, Integer> {

    public List<Debtor> getDebtorsForInvoice(Invoice invoice) throws SQLException;

}
