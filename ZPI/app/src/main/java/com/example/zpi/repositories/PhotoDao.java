package com.example.zpi.repositories;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.Photo;
import com.example.zpi.models.Trip;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.IInvoiceDao;
import com.example.zpi.repositories_interfaces.IPhotoDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class PhotoDao extends BaseDaoImpl<Photo, Integer> implements IPhotoDao {
    public PhotoDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Photo.class);
    }


    @Override
    public List<Photo> getPhotosFromTrip(Trip trip) throws SQLException {
        return super.queryForEq("TripID", trip.getID());
    }
}
