package com.example.zpi.models;
import java.io.Serializable;
import javax.persistence.*;
@Entity
@Table(name="Comment")
public class Comment implements Serializable {
	public Comment() {
	}
	
	@Column(name="ID", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(generator="COMMENT_ID_GENERATOR")	
	private int ID;
	
	@Column(name="Content", nullable=false, length=255)	
	private String content;
	
	@ManyToOne(targetEntity= ForumThread.class, fetch=FetchType.LAZY)
	@JoinColumn(name="ThreadID", referencedColumnName="ID", nullable=false)
	private ForumThread thread;
	
	@ManyToOne(targetEntity= User.class, fetch=FetchType.LAZY)
	@JoinColumn(name="UserID", referencedColumnName="ID", nullable=false)
	private User user;
	
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
	
	public void setThread(ForumThread value) {
		this.thread = value;
	}
	
	public ForumThread getThread() {
		return thread;
	}
	
	public void setUser(User value) {
		this.user = value;
	}
	
	public User getUser() {
		return user;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
