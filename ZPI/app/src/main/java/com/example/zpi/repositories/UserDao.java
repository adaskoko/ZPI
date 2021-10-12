package com.example.zpi.repositories;

import com.example.zpi.BaseConnection;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;
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
}
