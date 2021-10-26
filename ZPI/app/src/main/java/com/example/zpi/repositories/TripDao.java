package com.example.zpi.repositories;

import com.example.zpi.data_handling.BaseConnection;
import com.example.zpi.models.Role;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripParticipant;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.ITripDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TripDao extends BaseDaoImpl<Trip, Integer> implements ITripDao {

    public TripDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Trip.class);
    }

    @Override
    public void createTrip(String name, String description, Date startDate, Date endDate, User user) throws SQLException {
        Trip trip = new Trip(name, description, startDate, endDate);
        this.create(trip);

        Role role = DaoManager.createDao(connectionSource, Role.class).queryForEq("ID", 1).get(0);

        TripParticipant tripParticipant = new TripParticipant(user, role, trip);
        new TripParticipantDao(BaseConnection.getConnectionSource()).create(tripParticipant);
    }

    @Override
    public void addUserToTrip(Trip trip, User user, Role role) throws SQLException {
        TripParticipant tripParticipant = new TripParticipant(user, role, trip);
        new TripParticipantDao(BaseConnection.getConnectionSource()).create(tripParticipant);
    }

    @Override
    public List<Trip> getTripsForUser(User user) throws SQLException {
        List<TripParticipant> tripParticipants = new TripParticipantDao(BaseConnection.getConnectionSource()).getByUser(user);

        List<Trip> trips = new ArrayList<>();

        for (TripParticipant tripPart : tripParticipants){
            Trip trip = tripPart.getTrip();
            this.refresh(trip);
            trips.add(trip);
        }

        return trips;
    }

    @Override
    public Trip getCurrentTripForUser(User user) throws SQLException {
        List<TripParticipant> tripParticipants = new TripParticipantDao(BaseConnection.getConnectionSource()).getByUser(user);

        List<Trip> trips = new ArrayList<>();

        for (TripParticipant tripPart : tripParticipants){
            Trip trip = tripPart.getTrip();
            this.refresh(trip);

            Date now = new Date(System.currentTimeMillis());
            if(trip.getEndDate().after(now) && now.after(trip.getStartDate())){
                trips.add(trip);
            }
        }
        if(trips.size() > 0) return trips.get(0);
        return null;
    }

    @Override
    public List<Trip> getPastTripsForUser(User user) throws SQLException {
        List<TripParticipant> tripParticipants = new TripParticipantDao(BaseConnection.getConnectionSource()).getByUser(user);

        List<Trip> trips = new ArrayList<>();

        for (TripParticipant tripPart : tripParticipants){
            Trip trip = tripPart.getTrip();
            this.refresh(trip);

            Date now = new Date(System.currentTimeMillis());
            if(now.after(trip.getEndDate())){
                trips.add(trip);
            }
        }

        return trips;
    }

    @Override
    public List<Trip> getFutureTripsForUser(User user) throws SQLException {
        List<TripParticipant> tripParticipants = new TripParticipantDao(BaseConnection.getConnectionSource()).getByUser(user);

        List<Trip> trips = new ArrayList<>();

        for (TripParticipant tripPart : tripParticipants){
            Trip trip = tripPart.getTrip();
            this.refresh(trip);

            Date now = new Date(System.currentTimeMillis());
            if(trip.getStartDate().after(now)){
                trips.add(trip);
            }
        }

        return trips;
    }
}