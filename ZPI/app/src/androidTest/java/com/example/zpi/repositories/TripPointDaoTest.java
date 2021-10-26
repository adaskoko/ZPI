package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointType;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.*;

public class TripPointDaoTest {

    ConnectionSource connectionSource;

    @Before
    public void initialize(){
        connectionSource = BaseConnection.getConnectionSource();
    }

    @Test
    @Ignore("Saving to database")
    public void createTripPoint() {
        try {
            TripPointType tripPointType = DaoManager.createDao(connectionSource, TripPointType.class).queryForEq("ID", 1).get(0);
            TripPointLocation location = new TripPointLocation(1, 2, "adres");
            Trip trip = DaoManager.createDao(connectionSource, Trip.class).queryForEq("ID", 1).get(0);

            new TripPointDao(connectionSource).createTripPoint("testowy", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "remarks", trip, location, tripPointType);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void addInvoiceToTripPoint() {
    }

    @Test
    //@Ignore("Saving to database")
    public void addUserToTripPoint() {
        try {
            User user = new UserDao(connectionSource).queryForEq("ID", 24).get(0);
            TripPoint tripPoint = new TripPointDao(connectionSource).queryForEq("ID", 2).get(0);

            new TripPointDao(connectionSource).addUserToTripPoint(user, tripPoint, 123.45);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}