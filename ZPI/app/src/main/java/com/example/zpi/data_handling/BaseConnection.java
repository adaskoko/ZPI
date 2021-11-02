package com.example.zpi.data_handling;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;

public class BaseConnection {

    private static ConnectionSource connectionSource;

    public static ConnectionSource getConnectionSource() {
        if (connectionSource == null){
            String databaseUrl = "jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11439521";
            try {
                connectionSource = new JdbcConnectionSource(databaseUrl, "sql11439521", "wdsA5B6LFC");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return connectionSource;
    }

    public static void closeConnection(){
        try {
            ConnectionSource connectionSource = BaseConnection.getConnectionSource();
            connectionSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
