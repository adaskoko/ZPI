package com.example.zpi.data_handling;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;

public class BaseConnection {

    private static ConnectionSource connectionSource;
    private static FirebaseStorage storageInstance;
    private static StorageReference videoStorageReference;

    public static ConnectionSource getConnectionSource() {
        if (connectionSource == null){
            //String databaseUrl = "jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11439521";
            String databaseUrl = "jdbc:mysql://db4free.net:3306/zpi_test_db";
            try {
                //connectionSource = new JdbcConnectionSource(databaseUrl, "sql11439521", "wdsA5B6LFC");
                connectionSource = new JdbcConnectionSource(databaseUrl, "zpi_test_user", "zpi_test_password");
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

    public static FirebaseStorage getStorageInstance(){
        if (storageInstance == null)
            storageInstance = FirebaseStorage.getInstance();
        return storageInstance;
    }

}
