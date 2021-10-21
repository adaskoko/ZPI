package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Role;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.DaoManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class TripDaoTest {

    User user;
    @Before
    public void initialize(){
        try {
            user = new UserDao(BaseConnection.getConnectionSource()).findByEmail("a");

            Assert.assertEquals("ewa", user.getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void getTripsForUser() {
        try {
            List<Trip> trips = new TripDao(BaseConnection.getConnectionSource()).getTripsForUser(user);

            Assert.assertEquals(4, trips.size());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void getCurrentTripForUser() {
        try {
            Trip trip = new TripDao(BaseConnection.getConnectionSource()).getCurrentTripForUser(user);

            Assert.assertEquals("test", trip.getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void getPastTripsForUser() {
        try {
            List<Trip> trips = new TripDao(BaseConnection.getConnectionSource()).getPastTripsForUser(user);

            Assert.assertEquals(2, trips.size());
            Assert.assertEquals("testOld", trips.get(0).getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void getFutureTripsForUser() {
        try {
            List<Trip> trips = new TripDao(BaseConnection.getConnectionSource()).getFutureTripsForUser(user);

            Assert.assertEquals(1, trips.size());
            Assert.assertEquals("testFuture", trips.get(0).getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    @Ignore("Saving to real database - one test run only")
    public void createTrip() {
        try {
            new TripDao(BaseConnection.getConnectionSource()).createTrip("testCreate", "desc", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), user);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    @Ignore("Saving to database - one test run only")
    public void addUserToTrip() {
        try {
            Role role = DaoManager.createDao(BaseConnection.getConnectionSource(), Role.class).queryForEq("ID", 1).get(0);
            Trip trip = DaoManager.createDao(BaseConnection.getConnectionSource(), Trip.class).queryForEq("ID", 1).get(0);

            new TripDao(BaseConnection.getConnectionSource()).addUserToTrip(trip, user, role);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}