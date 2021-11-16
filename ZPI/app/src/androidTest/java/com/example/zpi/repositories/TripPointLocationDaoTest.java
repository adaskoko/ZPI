package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class TripPointLocationDaoTest {

    @Test
    public void getLocationForTripPoint() {
        try {
            TripPoint tripPoint = new TripPointDao(BaseConnection.getConnectionSource()).queryForId(2);
            TripPointLocation location = new TripPointLocationDao(BaseConnection.getConnectionSource()).getLocationForTripPoint(tripPoint);

            Assert.assertEquals("elo", location.getGoogleID());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}