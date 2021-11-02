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
