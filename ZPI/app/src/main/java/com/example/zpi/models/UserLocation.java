package com.example.zpi.models;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
@Entity
@Table(name="UserLocation")
public class UserLocation implements Serializable {
	public UserLocation() {
	}

	public UserLocation(double latitude, double longitude, Date time, User user) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
		this.user = user;
	}

	@Column(name="ID", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(generator="USERLOCATION_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Latitude", nullable=false)	
	private double latitude;
	
	@Column(name="Longitude", nullable=false)	
	private double longitude;
	
	@Column(name="Time", nullable=false)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date time;
	
	@ManyToOne(targetEntity=User.class, fetch=FetchType.LAZY)	
	@JoinColumn(name="UserID", referencedColumnName="ID", nullable=false)
	private User user;
	
	private void setID(int value) {
		this.ID = value;
	}
	
	public int getID() {
		return ID;
	}

	public void setLatitude(double value) {
		this.latitude = value;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLongitude(double value) {
		this.longitude = value;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setTime(java.util.Date value) {
		this.time = value;
	}
	
	public java.util.Date getTime() {
		return time;
	}
	
	public void setUser(User value) {
		this.user = value;
	}
	
	public User getUser() {
		return user;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
