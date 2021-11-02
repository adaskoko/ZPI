package com.example.zpi.repositories;

import com.example.zpi.models.Invoice;
import com.example.zpi.models.Trip;
import com.example.zpi.models.TripPoint;
import com.example.zpi.models.TripPointLocation;
import com.example.zpi.models.TripPointParticipant;
import com.example.zpi.models.TripPointType;
import com.example.zpi.models.User;
import com.example.zpi.repositories_interfaces.ITripPointDao;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripPointDao extends BaseDaoImpl<TripPoint, Integer> implements ITripPointDao {
    public TripPointDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, TripPoint.class);
    }

    @Override
    public void createTripPoint(String name, Date arrivalDate, Date departureDate, String remarks, Trip trip, TripPointLocation location, TripPointType type) throws SQLException {
        TripPoint tripPoint = new TripPoint(name, arrivalDate, departureDate, remarks, type, location, trip);

        new TripPointLocationDao(connectionSource).create(location); //TODO: chcemy zawsze zapisywac lokalizacje w bazie czy chcewmy wyszukiwac czy juz istnieje?
        this.create(tripPoint);
    }

    @Override
    public void addInvoiceToTripPoint(Invoice invoice, TripPoint tripPoint) throws SQLException {
        //tripPoint.setInvoice(invoice);
        invoice.setTripPoint(tripPoint);

        //this.update(tripPoint);
        new InvoiceDao(connectionSource).update(invoice);
    }

    @Override
    public void addUserToTripPoint(User user, TripPoint tripPoint, double charge) throws SQLException {
        TripPointParticipant participant = new TripPointParticipant(charge, user, tripPoint);
        new TripPointParticipantDao(connectionSource).create(participant);
    }

    @Override
    public List<TripPoint> getTripPointsByUser(User user) throws SQLException {
        List<TripPointParticipant> participants = new TripPointParticipantDao(connectionSource).getParticipantsByUser(user);
        List<TripPoint> tripPoints = new ArrayList<>();

        for(TripPointParticipant part : participants){
            TripPoint point = part.getTripPoint();
            this.refresh(point);
            tripPoints.add(point);
        }

        return tripPoints;
    }

    @Override
    public List<TripPoint> getTripPointsByTrip(Trip trip) throws SQLException {
        List<TripPoint> points = super.queryForEq("TripID", trip.getID());

        TripPointTypeDao dao = new TripPointTypeDao(connectionSource);

        for(TripPoint point : points){
            dao.refresh(point.getTripPointType());
        }

        return points;
    }

    @Override
    public void removeUserFromTripPoint(User user, TripPoint tripPoint) throws SQLException {
        TripPointParticipantDao dao = new TripPointParticipantDao(connectionSource);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("UserID", user.getID());
        data.put("TripPointID", tripPoint.getID());
        List<TripPointParticipant> participants = dao.queryForFieldValues(data);
        if (participants.size() > 0)
            dao.delete(participants.get(0));
    }
}
