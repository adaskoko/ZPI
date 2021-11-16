package com.example.zpi.repositories;

import android.text.format.DateUtils;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
    public void removeUserFromTripPoint(User user, TripPoint tripPoint) throws SQLException {
        TripPointParticipantDao dao = new TripPointParticipantDao(connectionSource);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("UserID", user.getID());
        data.put("TripPointID", tripPoint.getID());
        List<TripPointParticipant> participants = dao.queryForFieldValues(data);
        if (participants.size() > 0)
            dao.delete(participants.get(0));
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
    public List<TripPoint> getTripPointsForToday(Trip trip, Date date) throws SQLException {
        if (date == null)
            date = new Date();

        List<TripPoint> points = super.queryForEq("TripID", trip.getID());

        TripPointTypeDao dao = new TripPointTypeDao(connectionSource);

        List<TripPoint> accommodations = new ArrayList<>();
        List<TripPoint> attractions = new ArrayList<>();

        for(TripPoint point : points){
            dao.refresh(point.getTripPointType());

            if (point.getTripPointType().getID() == 1){ //Atrakcja
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

                if (fmt.format(date).equals(fmt.format(point.getArrivalDate()))){
                    attractions.add(point);
                }
            } else { //Nocleg
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date date1 = cal.getTime();

                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                Date date2 = cal.getTime();

                if (point.getArrivalDate().before(date2) && point.getDepartureDate().after(date1)){
                    accommodations.add(point);
                }
            }
        }

        attractions.sort(new Comparator<TripPoint>() {
            public int compare(TripPoint o1, TripPoint o2) {
                return o1.getArrivalDate().compareTo(o2.getArrivalDate());
            }
        });

        accommodations.sort(new Comparator<TripPoint>() {
            public int compare(TripPoint o1, TripPoint o2) {
                return o1.getArrivalDate().compareTo(o2.getArrivalDate());
            }
        });

        if (accommodations.size() == 1) {
            attractions.add(0, accommodations.get(0));
            attractions.add(0, accommodations.get(0));
        }else if (accommodations.size() > 1) {
            attractions.add(0, accommodations.get(0));
            attractions.add(1, accommodations.get(1));
        }

        return attractions;
    }

}
