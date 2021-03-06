package com.example.zpi.models;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
@Entity
@Table(name="ProductToTake")
public class ProductToTake implements Serializable {
	public ProductToTake() {
	}

	public ProductToTake(String name, User user, Trip trip) {
		this.name = name;
		this.user = user;
		this.trip = trip;
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="PRODUCTTOTAKE_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Name", nullable=false, length=255)	
	private String name;
	
	@Column(name="Ammount", nullable=false, length=10)	
	private int ammount;

	@Column(name="Done", nullable=true)
	private boolean done;
	
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

	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAmmount(int value) {
		this.ammount = value;
	}
	
	public int getAmmount() {
		return ammount;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
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
