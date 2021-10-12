package com.example.zpi.models;
import java.io.Serializable;
import javax.persistence.*;
@Entity
@Table(name="Thread")
public class ForumThread implements Serializable {
	public ForumThread() {
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="THREAD_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Summary", nullable=false, length=255)	
	private String summary;
	
	@ManyToOne(targetEntity= ThreadType.class, fetch=FetchType.LAZY)
	@JoinColumn(name="ThreadTypeID", referencedColumnName="ID", nullable=false)
	private ThreadType threadType;
	
	@ManyToOne(targetEntity= Trip.class, fetch=FetchType.LAZY)
	@JoinColumn(name="TripID", referencedColumnName="ID", nullable=false)
	private Trip trip;
	
	private void setID(int value) {
		this.ID = value;
	}
	
	public int getID() {
		return ID;
	}

	public void setSummary(String value) {
		this.summary = value;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setThreadType(ThreadType value) {
		this.threadType = value;
	}
	
	public ThreadType getThreadType() {
		return threadType;
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
