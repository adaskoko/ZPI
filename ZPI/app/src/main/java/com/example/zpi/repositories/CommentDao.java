package com.example.zpi.repositories;

import com.example.zpi.models.Comment;
import com.example.zpi.models.ForumThread;
import com.example.zpi.repositories_interfaces.ICommentDao;
import com.example.zpi.repositories_interfaces.IForumThreadDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class CommentDao extends BaseDaoImpl<Comment, Integer> implements ICommentDao {

    public CommentDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Comment.class);
    }
}
