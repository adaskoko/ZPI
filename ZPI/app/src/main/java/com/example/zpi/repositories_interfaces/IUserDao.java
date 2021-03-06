package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public interface IUserDao extends Dao<User, Integer> {
    public User findByEmail(String email) throws SQLException;
    public List<User> findByNameAndSurname(String name, String surname) throws SQLException;
    public List<User> getUsersFromTrip(Trip trip) throws SQLException;
    public List<User> getUsersByTripPoint(TripPoint tripPoint) throws SQLException;
    public List<User> getAllUsers() throws SQLException;
}
