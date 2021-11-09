package com.example.zpi.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Photo")
public class Photo implements Serializable {

    public Photo() {
    }

    public Photo(String url, User user, Trip trip) {
        this.url = url;
        this.user = user;
        this.trip = trip;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    @Column(name="ID", nullable=false, unique=true, length=10)
    @Id
    @GeneratedValue(generator="PHOTO_ID_GENERATOR")
    private int ID;

    @Column(name="Url", nullable=false)
    private String url;

    @ManyToOne(targetEntity= User.class, fetch= FetchType.LAZY)
    @JoinColumn(name="UserID", referencedColumnName="ID", nullable=false)
    private User user;

    @ManyToOne(targetEntity= Trip.class, fetch= FetchType.LAZY)
    @JoinColumn(name="TripID", referencedColumnName="ID", nullable=false)
    private Trip trip;

}
