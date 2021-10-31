package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.TripPointType;
import com.j256.ormlite.support.ConnectionSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class TripPointTypeDaoTest {

    ConnectionSource connectionSource;

    @Before
    public void initialize(){
        connectionSource = BaseConnection.getConnectionSource();
    }

    @Test
    public void getTripPointTypes(){
        try {
            List<TripPointType> types = new TripPointTypeDao(connectionSource).queryForAll();

            Assert.assertEquals(2, types.size());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Test
    public void getTripPointTypeByName() {
        try {
            TripPointType type = new TripPointTypeDao(connectionSource).getTripPointTypeByName("Nocleg");
            Assert.assertEquals(2, type.getID());

            type = new TripPointTypeDao(connectionSource).getTripPointTypeByName("elo");
            assertNull(type);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}