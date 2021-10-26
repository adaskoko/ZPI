package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.PreparationPoint;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.j256.ormlite.support.ConnectionSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class PreparationPointDaoTest {

    ConnectionSource connectionSource;

    @Before
    public void initialize(){
        connectionSource = BaseConnection.getConnectionSource();
    }

    @Test
    public void getPreparationPointsByTrip() {
        try {
            Trip trip = new TripDao(connectionSource).queryForEq("ID", 1).get(0);
            User user = new UserDao(connectionSource).queryForEq("ID", 24).get(0);


            PreparationPointDao prepDao = new PreparationPointDao(connectionSource);

            PreparationPoint preparationPoint = new PreparationPoint("punkt testowy 2", "opis testowy", new Date(System.currentTimeMillis()), user, trip);
            prepDao.create(preparationPoint);

            List<PreparationPoint> points = prepDao.getPreparationPointsByTrip(trip);
            Assert.assertEquals(2, points.size());
            Assert.assertEquals("punkt testowy 2", points.get(1).getName());

            prepDao.delete(preparationPoint);
            points = prepDao.getPreparationPointsByTrip(trip);
            Assert.assertEquals(1, points.size());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}