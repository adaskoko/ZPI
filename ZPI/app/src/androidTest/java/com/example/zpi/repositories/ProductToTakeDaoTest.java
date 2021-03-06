package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class ProductToTakeDaoTest {

    ConnectionSource connectionSource;

    @Before
    public void initialize(){
        connectionSource = BaseConnection.getConnectionSource();
    }

    @Test
    public void getProductsByTrip() {
        try {
            Trip trip = DaoManager.createDao(connectionSource, Trip.class).queryForEq("ID", 1).get(0);
            List<ProductToTake> products = new ProductToTakeDao(connectionSource).getProductsByTrip(trip);

            Assert.assertEquals(2, products.size());
            Assert.assertEquals("a", products.get(0).getUser().getEmail());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    @Ignore("Saving to database")
    public void createTest(){
        try {
            Trip trip = DaoManager.createDao(connectionSource, Trip.class).queryForEq("ID", 1).get(0);
            User user = new UserDao(BaseConnection.getConnectionSource()).findByEmail("a");

            ProductToTake product = new ProductToTake("test done", user, trip);
            product.setDone(true);

            new ProductToTakeDao(connectionSource).create(product);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}