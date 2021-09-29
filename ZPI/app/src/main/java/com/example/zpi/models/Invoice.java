package com.example.zpi.models;
import java.io.Serializable;
import javax.persistence.*;
@Entity
@Table(name="Invoice")
public class Invoice implements Serializable {
	public Invoice() {
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="INVOICE_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Price", nullable=false)	
	private double price;
	
	@ManyToOne(targetEntity= User.class, fetch=FetchType.LAZY)
	@JoinColumn(name="UserID", referencedColumnName="ID", nullable=false)
	private User user;
	
	@OneToOne(optional=false, targetEntity= TripPoint.class, fetch=FetchType.LAZY)
	@JoinColumn(name="TripPointID", referencedColumnName="ID", nullable=false)
	private TripPoint tripPoint;
	
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
