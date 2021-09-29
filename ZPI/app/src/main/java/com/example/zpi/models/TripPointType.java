package com.example.zpi.models;
import java.io.Serializable;
import javax.persistence.*;
@Entity
@Table(name="TripPointType")
public class TripPointType implements Serializable {
	public TripPointType() {
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="TRIPPOINTTYPE_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Name", nullable=false, unique=true, length=255)	
	private String name;
	
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
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
