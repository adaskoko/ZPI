package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.Photo;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public interface IPhotoDao extends Dao<Photo, Integer> {
    public List<Photo> getPhotosFromTrip(Trip trip) throws SQLException;
}
