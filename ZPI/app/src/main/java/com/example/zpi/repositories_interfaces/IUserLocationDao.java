package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.User;
import com.example.zpi.models.UserLocation;
import com.example.zpi.repositories.UserLocationDao;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public interface IUserLocationDao extends Dao<UserLocation, Integer> {
    public UserLocation getUserLocationByUser(User user) throws SQLException;
}
