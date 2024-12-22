package com.smart.entities;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Contact")
public class Contact {
@Id
@GeneratedValue(strategy=GenerationType.AUTO)
	private int cid;

public Contact() {
	super();
	// TODO Auto-generated constructor stub
}

public int getCid() {
	return cid;
}

public void setCid(int cid) {
	this.cid = cid;
}

public String getsecondname() {
	return secondname;
}

public void setsecondname(String nickname) {
	this.secondname = nickname;
}

public String getPhoneno() {
	return phoneno;
}

public void setPhoneno(String phoneno) {
	this.phoneno = phoneno;
}

public String getDes() {
	return des;
}

public void setDes(String des) {
	this.des = des;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getWork() {
	return work;
}

public void setWork(String work) {
	this.work = work;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}


  public String getImage() { 
	  return image;
	  }
 

public void setImage(String image) {
	this.image = image;
}

private String secondname;
private String phoneno;
@Column(length=100)
private String des;

private String name;

private String work;

private String email;
	
private String image;	
	


	@ManyToOne(cascade = CascadeType.ALL)
private User user;

public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}

	
	@Override
	public boolean equals(Object obj)
	{
		return this.cid==((Contact)obj).getCid();
	}
	
	
}
