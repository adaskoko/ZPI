package com.example.zpi.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

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
public class MultimediaFile implements Serializable {

    public MultimediaFile() {
    }

    public MultimediaFile(String url, User user, Trip trip, Boolean isPhoto, Date creationDate) {
        this.url = url;
        this.user = user;
        this.trip = trip;
        this.isPhoto = isPhoto;
        this.creationDate = creationDate;
    }

    public int getID() {
        return ID;
    }

    @Column(name="ID", nullable=false, unique=true, length=10)
    @Id
    @GeneratedValue(generator="PHOTO_ID_GENERATOR")
    private int ID;

    @Column(name="Url", nullable=false)
    private String url;

    @Column(name="IsPhoto", nullable=false)
    private Boolean isPhoto;

    @Column(name="CreationDate", nullable=false)
    private Date creationDate;

    @ManyToOne(targetEntity= User.class, fetch= FetchType.LAZY)
    @JoinColumn(name="UserID", referencedColumnName="ID", nullable=false)
    private User user;

    @ManyToOne(targetEntity= Trip.class, fetch= FetchType.LAZY)
    @JoinColumn(name="TripID", referencedColumnName="ID", nullable=false)
    private Trip trip;

    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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

    public Boolean getPhoto() {
        return isPhoto;
    }

    public void setPhoto(Boolean photo) {
        isPhoto = photo;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

}
