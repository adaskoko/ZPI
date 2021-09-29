package com.example.zpi.models;
import java.io.Serializable;
import javax.persistence.*;
@Entity
@Table(name="TripPoint")
public class TripPoint implements Serializable {
	public TripPoint() {
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="TRIPPOINT_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Name", nullable=false, length=255)	
	private String name;
	
	@Column(name="ArrivalDate", nullable=false)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date arrivalDate;
	
	@Column(name="DepartureDate", nullable=true)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date departureDate;
	
	@Column(name="Remarks", nullable=true, length=255)	
	private String remarks;
	
	@ManyToOne(targetEntity= TripPointType.class, fetch=FetchType.LAZY)
	@JoinColumn(name="TripPointTypeID", referencedColumnName="ID", nullable=false)
	private TripPointType tripPointType;
	
	@OneToOne(optional=false, targetEntity= TripPointLocation.class, fetch=FetchType.LAZY)
	@JoinColumn(name="TripPointLocationID", referencedColumnName="ID", nullable=false)
	private TripPointLocation tripPointLocation;
	
	@ManyToOne(targetEntity=Trip.class, fetch=FetchType.LAZY)	
	@JoinColumn(name="TripID", referencedColumnName="ID", nullable=false)
	private Trip trip;

	@OneToOne(mappedBy="tripPoint", targetEntity=Invoice.class, fetch=FetchType.LAZY)	
	private Invoice invoice;
	
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
	
	public void setArrivalDate(java.util.Date value) {
		this.arrivalDate = value;
	}
	
	public java.util.Date getArrivalDate() {
		return arrivalDate;
	}
	
	public void setDepartureDate(java.util.Date value) {
		this.departureDate = value;
	}
	
	public java.util.Date getDepartureDate() {
		return departureDate;
	}
	
	public void setRemarks(String value) {
		this.remarks = value;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public void setTripPointType(TripPointType value) {
		this.tripPointType = value;
	}
	
	public TripPointType getTripPointType() {
		return tripPointType;
	}
	
	public void setTripPointLocation(TripPointLocation value) {
		this.tripPointLocation = value;
	}
	
	public TripPointLocation getTripPointLocation() {
		return tripPointLocation;
	}
	
	public void setTrip(Trip value) {
		this.trip = value;
	}
	
	public Trip getTrip() {
		return trip;
	}
	
	public void setInvoice(Invoice value) {
		this.invoice = value;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
