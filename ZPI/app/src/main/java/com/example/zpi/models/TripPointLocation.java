package com.example.zpi.models;
import java.io.Serializable;
import javax.persistence.*;
@Entity
@Table(name="TripPointLocation")
public class TripPointLocation implements Serializable {
	public TripPointLocation() {
	}

	public TripPointLocation(double latitude, double longitude, String address) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="TRIPPOINTLOCATION_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Latitude", nullable=false)	
	private double latitude;
	
	@Column(name="Longitude", nullable=false)	
	private double longitude;
	
	@Column(name="Address", nullable=false, length=255)	
	private String address;
	
	/*@OneToOne(mappedBy="tripPointLocation", targetEntity=TripPoint.class, fetch=FetchType.LAZY)
	private TripPoint tripPoint;*/

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
	
	public void setAddress(String value) {
		this.address = value;
	}
	
	public String getAddress() {
		return address;
	}
	
	/*public void setTripPoint(TripPoint value) {
		this.tripPoint = value;
	}
	
	public TripPoint getTripPoint() {
		return tripPoint;
	}*/
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
