package com.example.zpi.repositories;

import com.example.zpi.models.ForumThread;
import com.example.zpi.models.Role;
import com.example.zpi.models.ThreadType;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.IForumThreadDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class ForumThreadDao extends BaseDaoImpl<ForumThread, Integer> implements IForumThreadDao {
    public ForumThreadDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ForumThread.class);
    }

    @Override
    public void createRegulatThread(String summary, String title, Trip trip) throws SQLException {
        ThreadType type = DaoManager.createDao(connectionSource, ThreadType.class).queryForEq("ID", 1).get(0);
        ForumThread ft=new ForumThread(summary, title, trip, type);
        this.create(ft);
    }

    @Override
    public List<ForumThread> getThreadsForTrip(Trip trip) throws SQLException {
        return super.queryForEq("TripID", trip.getID());
    }
}
