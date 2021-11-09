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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        new TripPartcicipantDao(BaseConnection.getConnectionSource()).create(tripParticipant);
    }

    @Override
    public void createTrip(Trip trip, User user) throws SQLException {
        this.create(trip);

        Role role = DaoManager.createDao(connectionSource, Role.class).queryForEq("ID", 1).get(0);

        TripParticipant tripParticipant = new TripParticipant(user, role, trip);
        new TripPartcicipantDao(BaseConnection.getConnectionSource()).create(tripParticipant);
    }

    @Override
    public void addUserToTrip(Trip trip, User user, Role role) throws SQLException {
        TripParticipant tripParticipant = new TripParticipant(user, role, trip);
        new TripPartcicipantDao(BaseConnection.getConnectionSource()).create(tripParticipant);
    }

    @Override
    public void addRegularParticipant(Trip trip, User user) throws SQLException {
        Role participant=DaoManager.createDao(connectionSource, Role.class).queryForEq("ID", 2).get(0);
        TripParticipant tripParticipant=new TripParticipant(user, participant, trip);
        new TripPartcicipantDao(BaseConnection.getConnectionSource()).create(tripParticipant);
    }

    @Override
    public List<Trip> getTripsForUser(User user) throws SQLException {
        List<TripParticipant> tripParticipants = new TripPartcicipantDao(BaseConnection.getConnectionSource()).getByUser(user);

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
        List<TripParticipant> tripParticipants = new TripPartcicipantDao(BaseConnection.getConnectionSource()).getByUser(user);

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
        List<TripParticipant> tripParticipants = new TripPartcicipantDao(BaseConnection.getConnectionSource()).getByUser(user);

        List<Trip> trips = new ArrayList<>();

        for (TripParticipant tripPart : tripParticipants){
            Trip trip = tripPart.getTrip();
            this.refresh(trip);

            Date now = new Date(System.currentTimeMillis());
            if(now.after(trip.getEndDate())){
                trips.add(trip);
            }
        }
        trips.sort(Comparator.comparing(Trip::getStartDate).reversed());

        return trips;
    }

    @Override
    public List<Trip> getFutureTripsForUser(User user) throws SQLException {
        List<TripParticipant> tripParticipants = new TripPartcicipantDao(BaseConnection.getConnectionSource()).getByUser(user);

        List<Trip> trips = new ArrayList<>();

        for (TripParticipant tripPart : tripParticipants){
            Trip trip = tripPart.getTrip();
            this.refresh(trip);

            Date now = new Date(System.currentTimeMillis());
            if(trip.getStartDate().after(now)){
                trips.add(trip);
            }
        }
        trips.sort(Comparator.comparing(Trip::getStartDate));

        return trips;
    }

    public List<List<Trip>> getPastAndFutureTripsForUser(User user) throws SQLException {
        List<TripParticipant> tripParticipants = new TripPartcicipantDao(BaseConnection.getConnectionSource()).getByUser(user);
        List<Trip> pastTrips = new ArrayList<>();
        List<Trip> futureTrips = new ArrayList<>();

        for (TripParticipant tripPart : tripParticipants){
            Trip trip = tripPart.getTrip();
            this.refresh(trip);

            Date now = new Date(System.currentTimeMillis());
            if(trip.getStartDate().after(now)){
                futureTrips.add(trip);
            } else if (trip.getEndDate().before(now)){
                pastTrips.add(trip);
            }
        }
        pastTrips.sort(Comparator.comparing(Trip::getStartDate));
        futureTrips.sort(Comparator.comparing(Trip::getStartDate));
        List<List<Trip>> allTrips = new ArrayList<>();
        allTrips.add(pastTrips);
        allTrips.add(futureTrips);
        return allTrips;
    }

    @Override
    public List<Trip> getTripByNameAndDate(String tripName, Date startDate, Date endDate) throws SQLException{
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("Name", tripName);
        values.put("StartDate", startDate);
        values.put("EndDate", endDate);
        return super.queryForFieldValues(values);
    }
}
