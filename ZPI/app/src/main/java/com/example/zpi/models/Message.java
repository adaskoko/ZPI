package com.example.zpi.models;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
@Entity
@Table(name="Message")
public class Message implements Serializable {
	public Message() {
	}

	public Message(String content, Date sendDate, User sender, User receiver){
		this.content=content;
		this.sendingDate=sendDate;
		this.sender=sender;
		this.receiver=receiver;
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="MESSAGE_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Content", nullable=false, length=255)	
	private String content;
	
	@Column(name="SendingDate", nullable=false)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date sendingDate;
	
	@ManyToOne(targetEntity= User.class, fetch=FetchType.LAZY)
	@JoinColumn(name="SenderID", referencedColumnName="ID", nullable=false)
	private User sender;
	
	@ManyToOne(targetEntity= User.class, fetch=FetchType.LAZY)
	@JoinColumn(name="ReceiverID", referencedColumnName="ID", nullable=false)
	private User receiver;
	
	private void setID(int value) {
		this.ID = value;
	}
	
	public int getID() {
		return ID;
	}

	public void setContent(String value) {
		this.content = value;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setSendingDate(java.util.Date value) {
		this.sendingDate = value;
	}
	
	public java.util.Date getSendingDate() {
		return sendingDate;
	}
	
	public void setSender(User value) {
		this.sender = value;
	}
	
	public User getSender() {
		return sender;
	}
	
	public void setReceiver(User value) {
		this.receiver = value;
	}
	
	public User getReceiver() {
		return receiver;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
