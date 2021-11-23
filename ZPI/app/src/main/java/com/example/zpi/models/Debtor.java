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
@Table(name="Debtor")
public class Debtor implements Serializable {

    public Debtor() {
    }

    public Debtor(User user, Invoice invoice) {
        this.user = user;
        this.invoice = invoice;
    }

    public int getID() {
        return ID;
    }

    @Column(name="ID", nullable=false, unique=true, length=10)
    @Id
    @GeneratedValue(generator="PHOTO_ID_GENERATOR")
    private int ID;

    @ManyToOne(targetEntity= User.class, fetch= FetchType.LAZY)
    @JoinColumn(name="UserID", referencedColumnName="ID", nullable=false)
    private User user;

    @ManyToOne(targetEntity= Invoice.class, fetch= FetchType.LAZY)
    @JoinColumn(name="InvoiceID", referencedColumnName="ID", nullable=false)
    private Invoice invoice;


    public void setID(int ID) {
        this.ID = ID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

}
