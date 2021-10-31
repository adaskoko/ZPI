package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class UserDaoTest {

    ConnectionSource connectionSource;

    @Before
    public void initialize(){
        connectionSource = BaseConnection.getConnectionSource();
    }

    @Test
    public void findByEmail() {
        try {
            User user = new UserDao(connectionSource).findByEmail("a");

            Assert.assertEquals(24, user.getID());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void getUsersFromTrip() {
        try {
            Trip trip = DaoManager.createDao(connectionSource, Trip.class).queryForEq("ID", 1).get(0);
            List<User> users = new UserDao(connectionSource).getUsersFromTrip(trip);

            Assert.assertEquals(1, users.size());
            Assert.assertEquals("a", users.get(0).getEmail());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}