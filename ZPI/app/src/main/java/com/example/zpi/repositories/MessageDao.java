package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Message;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.IMessageDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MessageDao extends BaseDaoImpl<Message, Integer> implements IMessageDao {

    public MessageDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Message.class);
    }

    @Override
    public List<User> getConvosForUser(User u) throws SQLException {
        List<User> results = new ArrayList<>();
        HashSet<Integer> ids = new HashSet<>();
        List<Message> messages = this.queryForAll();
        UserDao dao = new UserDao(BaseConnection.getConnectionSource());
        for (Message m : messages) {

            int senderID = m.getSender().getID();
            int receiverID = m.getReceiver().getID();

            if (senderID == u.getID() && ids.add(receiverID)) {
                User receiver = m.getReceiver();
                dao.refresh(receiver);
                results.add(receiver);
            } else if (receiverID == u.getID() && ids.add(senderID)) {
                User sender = m.getSender();
                dao.refresh(sender);
                results.add(sender);
            }
        }
        return results;
    }

    @Override
    public List<Message> getMessagesForConvo(User u1, User u2) throws SQLException {
        Map<String, Object> data1 = new HashMap<>();
        data1.put("SenderID", u1.getID());
        data1.put("ReceiverID", u2.getID());

        Map<String, Object> data2 = new HashMap<>();
        data2.put("SenderID", u2.getID());
        data2.put("ReceiverID", u1.getID());

        List<Message> list1;
        list1 = super.queryForFieldValues(data1);
        List<Message> list2 = super.queryForFieldValues(data2);
        List<Message> result = new ArrayList<>();
        result.addAll(list1);
        result.addAll(list2);
        result.sort(Comparator.comparing(Message::getSendingDate));
        return result;
    }

    @Override
    public Message getLastMessageFromList(List<Message> messages) {
        return messages.get(messages.size()-1);
    }

}
