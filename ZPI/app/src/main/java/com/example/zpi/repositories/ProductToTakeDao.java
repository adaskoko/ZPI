package com.example.zpi.repositories;

import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.IProductToTakeDao;
import com.example.zpi.repositories_interfaces.ITripDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class ProductToTakeDao extends BaseDaoImpl<ProductToTake, Integer> implements IProductToTakeDao {

    protected ProductToTakeDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ProductToTake.class);
    }

    @Override
    public List<ProductToTake> getProductsByTrip(Trip trip) throws SQLException {
        List<ProductToTake> products = super.queryForEq("TripID", trip.getID());

        UserDao userDao = new UserDao(connectionSource);
        for(ProductToTake product : products){
            userDao.refresh(product.getUser());
        }

        return products;
    }
}