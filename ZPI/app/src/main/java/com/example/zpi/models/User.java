package com.example.zpi.models;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;
@Entity
@Table(name="User")
public class User implements Serializable {
	public User() {
	}

	public User(String name, String surname, String email, String password) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.joiningDate = Calendar.getInstance().getTime();
	}

	public User(String name, String surname, String email) {
		this.name = name;
		this.surname = surname;
		this.email = email;
	}

	@Column(name="ID", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(generator="USER_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Name", nullable=false, length=255)	
	private String name;
	
	@Column(name="Surname", nullable=false, length=255)	
	private String surname;
	
	@Column(name="Email", nullable=false, unique=true, length=255)	
	private String email;
	
	@Column(name="Password", nullable=false, length=255)	
	private String password;
	
	@Column(name="JoiningDate", nullable=false)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date joiningDate;
	
	@Column(name="BirthDate", nullable=true)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date birthDate;
	
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
	
	public void setSurname(String value) {
		this.surname = value;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setEmail(String value) {
		this.email = value;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setPassword(String value) {
		this.password = value;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setJoiningDate(java.util.Date value) {
		this.joiningDate = value;
	}
	
	public java.util.Date getJoiningDate() {
		return joiningDate;
	}
	
	public void setBirthDate(java.util.Date value) {
		this.birthDate = value;
	}
	
	public java.util.Date getBirthDate() {
		return birthDate;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
