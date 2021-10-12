package com.example.zpi.repositories;

import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public interface IUserDao extends Dao<User, Integer> {
    public User findByEmail(String email) throws SQLException;
}
