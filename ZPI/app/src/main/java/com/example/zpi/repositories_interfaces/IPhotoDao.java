package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.MultimediaFile;
import com.example.zpi.models.Trip;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public interface IPhotoDao extends Dao<MultimediaFile, Integer> {
    public List<MultimediaFile> getPhotosFromTrip(Trip trip) throws SQLException;
}
