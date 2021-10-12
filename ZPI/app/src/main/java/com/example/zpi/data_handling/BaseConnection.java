package com.example.zpi.data_handling;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;

public class BaseConnection {

    public static ConnectionSource getConnectionSource() {
        String databaseUrl = "jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11439521";
        try {
            return new JdbcConnectionSource(databaseUrl, "sql11439521", "wdsA5B6LFC");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void closeConnection(ConnectionSource connectionSource){
        try {
            connectionSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
