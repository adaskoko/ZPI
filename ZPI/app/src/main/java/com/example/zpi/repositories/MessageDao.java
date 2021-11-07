package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Message;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.IMessageDao;
import com.example.zpi.repositories_interfaces.IUserDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDao extends BaseDaoImpl<Message, Integer> implements IMessageDao {

    public MessageDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Message.class);
    }

    @Override
    public List<User> getConvosForUser(User u) throws SQLException {
        List<User> results=new ArrayList<User>();
        List<Integer> ids=new ArrayList<Integer>();
        List<Message> messages=this.queryForAll();
        UserDao dao=new UserDao(BaseConnection.getConnectionSource());
        for(Message m:messages){
            User sender=m.getSender();
            dao.refresh(sender);
            User receiver=m.getReceiver();
            dao.refresh(receiver);
            if(sender.getID()==u.getID() && !ids.contains(receiver.getID())){
                results.add(receiver);
                ids.add(receiver.getID());
            }else if(receiver.getID()==u.getID() && !ids.contains(sender.getID())){
                results.add(sender);
                ids.add(sender.getID());
            }
        }
        return results;
    }

    @Override
    public List<Message> getMessagesForConvo(User u1, User u2) throws SQLException {
        Map<String, Object> data1=new HashMap<String, Object>();
        data1.put("SenderID", u1.getID());
        data1.put("ReceiverID", u2.getID());

        Map<String, Object> data2=new HashMap<String, Object>();
        data2.put("SenderID", u2.getID());
        data2.put("ReceiverID", u1.getID());

        List<Message> list1= null;
        try {
            list1 = super.queryForFieldValues(data1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        List<Message> list2=super.queryForFieldValues(data2);
        List<Message> result=new ArrayList<>();
        result.addAll(list1);
        result.addAll(list2);
        Collections.sort(result, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getSendingDate().compareTo(o2.getSendingDate());
            }
        });
        return result;
    }

    @Override
    public Message getLastMessageFromList(List<Message> messages){
        Message latestMessage=messages.get(0);
        for(Message m: messages){
            if(m.getSendingDate().after(latestMessage.getSendingDate())){
                latestMessage=m;
            }
        }
        return latestMessage;
    }

    private static class MyClass implements Comparable<Message>{
        private Date someDate;
        public Date getDate(){
            return someDate;
        }
        public void setDate(Date date){
            this.someDate=date;
        }
        @Override
        public int compareTo(Message message){
            return getDate().compareTo(message.getSendingDate());
        }
    }
}
