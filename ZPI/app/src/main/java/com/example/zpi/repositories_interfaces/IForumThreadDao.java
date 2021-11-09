package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.ForumThread;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public interface IForumThreadDao extends Dao<ForumThread, Integer> {

    public void createRegulatThread(String summary, String title, Trip trip) throws SQLException;
    public List<ForumThread> getThreadsForTrip(Trip trip) throws SQLException;
}
