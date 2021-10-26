package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.IUserDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao extends BaseDaoImpl<User, Integer> implements IUserDao {

    public UserDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, User.class);
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        List<User> userResult = super.queryForEq("Email", email);

        BaseConnection.closeConnection(connectionSource);

        if(userResult.size() > 0)
           return userResult.get(0);
        return null;
    }

    @Override
    public List<User> getUsersFromTrip(Trip trip) throws SQLException {
        List<TripParticipant> tripParticipants = new TripParticipantDao(BaseConnection.getConnectionSource()).getByTrip(trip);

        List<User> users = new ArrayList<>();

        for (TripParticipant tripPart : tripParticipants){
            User user = tripPart.getUser();
            this.refresh(user);
            users.add(user);
        }

        return users;
    }
}
