package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.IUserDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao extends BaseDaoImpl<User, Integer> implements IUserDao {

    public UserDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, User.class);
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        List<User> userResult = super.queryForEq("Email", email);

        //BaseConnection.closeConnection();

        if(userResult.size() > 0)
           return userResult.get(0);
        return null;
    }

    @Override
    public List<User> findByNameAndSurname(String name, String surname) throws SQLException {
        Map<String, Object> data=new HashMap<String, Object>();
        data.put("Name", name);
        data.put("Surname", surname);
        List<User> matchingUsers=super.queryForFieldValues(data);

        if(matchingUsers.size()>0) {
            return matchingUsers;
        }else{
            return null;
        }
    }

    @Override
    public List<User> getUsersFromTrip(Trip trip) throws SQLException {
        List<TripParticipant> tripParticipants = new TripPartcicipantDao(BaseConnection.getConnectionSource()).getByTrip(trip);

        List<User> users = new ArrayList<>();

        for (TripParticipant tripPart : tripParticipants){
            User user = tripPart.getUser();
            this.refresh(user);
            users.add(user);
        }

        return users;
    }

    @Override
    public List<User> getUsersByTripPoint(TripPoint tripPoint) throws SQLException {
        List<TripPointParticipant> participants = new TripPointParticipantDao(connectionSource).getParticipantsByTripPoint(tripPoint);
        List<User> users = new ArrayList<>();

        for(TripPointParticipant part : participants){
            User user = part.getUser();
            this.refresh(user);
            users.add(user);
        }

        return users;
    }
}
