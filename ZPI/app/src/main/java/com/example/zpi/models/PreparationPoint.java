package com.example.zpi.models;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
@Entity
@Table(name="PreparationPoint")
public class PreparationPoint implements Serializable {
	public PreparationPoint() {
	}

	public PreparationPoint(String name, String description, Date deadline, User user, Trip trip) {
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.user = user;
		this.trip = trip;
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="PREPARATIONPOINT_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Name", nullable=false, length=255)	
	private String name;
	
	@Column(name="Description", nullable=false, length=255)	
	private String description;
	
	@Column(name="Deadline", nullable=false)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date deadline;

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
	
	public void setDescription(String value) {
		this.description = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDeadline(java.util.Date value) {
		this.deadline = value;
	}
	
	public java.util.Date getDeadline() {
		return deadline;
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
	
	public void setTrip(Trip value) {
		this.trip = value;
	}
	
	public Trip getTrip() {
		return trip;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
