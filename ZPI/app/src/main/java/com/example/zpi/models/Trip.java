package com.example.zpi.models;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
@Entity
@Table(name="Trip")
public class Trip implements Serializable {
	public Trip() {
	}

	@Column(name="ID", nullable=false, unique=true, length=10)
	@Id
	@GeneratedValue(generator="TRIP_ID_GENERATOR")
	private int ID;

	@Column(name="Name", nullable=false, length=255)
	private String name;

	@Column(name="Description", nullable=false, length=255)
	private String description;

	@Column(name="StartDate", nullable=false)
	@Temporal(TemporalType.DATE)
	private java.util.Date startDate;

	@Column(name="EndDate", nullable=false)
	@Temporal(TemporalType.DATE)
	private java.util.Date endDate;

	public Trip(String name, String description, Date startDate, Date endDate) {
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
	}

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

	public void setStartDate(java.util.Date value) {
		this.startDate = value;
	}

	public java.util.Date getStartDate() {
		return startDate;
	}

	public void setEndDate(java.util.Date value) {
		this.endDate = value;
	}

	public java.util.Date getEndDate() {
		return endDate;
	}

	public String toString() {
		return String.valueOf(getID());
	}

}