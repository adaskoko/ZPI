package com.example.zpi.repositories;

import com.example.zpi.models.User;
import com.example.zpi.models.UserLocation;
import com.example.zpi.repositories_interfaces.IUserLocationDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class UserLocationDao extends BaseDaoImpl<UserLocation, Integer> implements IUserLocationDao {
    public UserLocationDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, UserLocation.class);
    }

    @Override
    public UserLocation getUserLocationByUser(User user) throws SQLException {
        List<UserLocation> userLocations = super.queryForEq("UserID", user.getID());

        if (userLocations.size() > 0)
            return userLocations.get(0);
        return null;
    }
}
