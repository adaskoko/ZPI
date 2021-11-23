package com.example.zpi.repositories;

import com.example.zpi.models.Debtor;
import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.example.zpi.repositories_interfaces.IDebtorDao;
import com.example.zpi.repositories_interfaces.IInvoiceDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class DebtorDao extends BaseDaoImpl<Debtor, Integer> implements IDebtorDao {
    public DebtorDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Debtor.class);
    }

    @Override
    public List<Debtor> getDebtorsForInvoice(Invoice invoice) throws SQLException {
        List<Debtor> debtors = super.queryForEq("InvoiceID", invoice.getID());
        UserDao userDao = new UserDao(connectionSource);
        for(Debtor debtor : debtors){
            userDao.refresh(debtor.getUser());
        }

        return debtors;
    }
}
