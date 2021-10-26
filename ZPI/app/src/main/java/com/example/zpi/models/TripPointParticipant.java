package com.example.zpi.models;
import java.io.Serializable;
import javax.persistence.*;
@Entity
@Table(name="TripPointParticipant")
public class TripPointParticipant implements Serializable {
	public TripPointParticipant() {
	}

	public TripPointParticipant(double charge, User user, TripPoint tripPoint) {
		this.charge = charge;
		this.user = user;
		this.tripPoint = tripPoint;
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="TRIPPOINTPARTICIPANT_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Charge", nullable=false)	
	private double charge;
	
	@ManyToOne(targetEntity= User.class, fetch=FetchType.LAZY)
	@JoinColumn(name="UserID", referencedColumnName="ID", nullable=false)
	private User user;
	
	@ManyToOne(targetEntity=TripPoint.class, fetch=FetchType.LAZY)	
	@JoinColumn(name="TripPointID", referencedColumnName="ID", nullable=false)
	private TripPoint tripPoint;

	private void setID(int value) {
		this.ID = value;
	}
	
	public int getID() {
		return ID;
	}

	public void setCharge(double value) {
		this.charge = value;
	}
	
	public double getCharge() {
		return charge;
	}
	
	public void setUser(User value) {
		this.user = value;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setTripPoint(TripPoint value) {
		this.tripPoint = value;
	}
	
	public TripPoint getTripPoint() {
		return tripPoint;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
