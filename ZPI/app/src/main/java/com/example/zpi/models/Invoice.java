package com.example.zpi.models;
import java.io.Serializable;
import javax.persistence.*;
@Entity
@Table(name="Invoice")
public class Invoice implements Serializable {
	public Invoice(double price, String description, User user, Trip trip) {
		this.price = price;
		this.description = description;
		this.user = user;
		this.trip = trip;
	}

	public Invoice() {
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="INVOICE_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Price", nullable=false)	
	private double price;

	@Column(name="Description", nullable=false, length=255)
	private String description;
	
	@ManyToOne(targetEntity= User.class, fetch=FetchType.LAZY)
	@JoinColumn(name="UserID", referencedColumnName="ID", nullable=false)
	private User user;

	@ManyToOne(targetEntity= Trip.class, fetch=FetchType.LAZY)
	@JoinColumn(name="TripID", referencedColumnName="ID", nullable=false)
	private Trip trip;
	
	private void setID(int value) {
		this.ID = value;
	}
	
	public int getID() {
		return ID;
	}

	public void setPrice(double value) {
		this.price = value;
	}
	
	public double getPrice() {
		return price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setUser(User value) {
		this.user = value;
	}
	
	public User getUser() {
		return user;
	}

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
