package com.example.zpi.repositories_interfaces;

import com.example.zpi.models.ProductToTake;
import com.example.zpi.models.Trip;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public interface IProductToTakeDao extends Dao<ProductToTake, Integer> {

    public List<ProductToTake> getProductsByTrip(Trip trip) throws SQLException;

}