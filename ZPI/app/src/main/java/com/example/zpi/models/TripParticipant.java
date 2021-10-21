package com.example.zpi.models;
import java.io.Serializable;
import javax.persistence.*;
@Entity
@Table(name="TripParticipant")
public class TripParticipant implements Serializable {
	public TripParticipant() {
	}

	public TripParticipant(User user, Role role, Trip trip) {
		this.user = user;
		this.role = role;
		this.trip = trip;
	}

	@Column(name="ID", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(generator="TRIPPARTICIPANT_ID_GENERATOR")	
	private int ID;
	
	@ManyToOne(targetEntity= User.class, fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="UserID", referencedColumnName="ID", nullable=false)
	private User user;
	
	@ManyToOne(targetEntity=Role.class, fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="RoleID", referencedColumnName="ID", nullable=false)
	private Role role;
	
	@ManyToOne(targetEntity=Trip.class, fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="TripID", referencedColumnName="ID", nullable=false)
	private Trip trip;
	
	private void setID(int value) {
		this.ID = value;
	}
	
	public int getID() {
		return ID;
	}

	public void setUser(User value) {
		this.user = value;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setRole(Role value) {
		this.role = value;
	}
	
	public Role getRole() {
		return role;
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
