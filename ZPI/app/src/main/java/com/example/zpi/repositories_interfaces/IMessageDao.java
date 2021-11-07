package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.Message;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public interface IMessageDao extends Dao<Message, Integer> {

    public List<User> getConvosForUser(User u) throws SQLException;
    public List<Message> getMessagesForConvo(User u1, User u2) throws SQLException;
    public Message getLastMessageFromList(List<Message> messages) throws SQLException;
}
