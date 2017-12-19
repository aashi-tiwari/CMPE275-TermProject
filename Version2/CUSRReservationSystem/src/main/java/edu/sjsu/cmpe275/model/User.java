package edu.sjsu.cmpe275.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
@Entity
public class User {
	
	@Id
	@Column(unique=true)
	private String email;
	
	@NotNull
	private String firstname;
	
	@NotNull
	private String lastname;
	
	@NotNull
	@Column(unique=true)
	private String username;
	
//	@OneToMany(mappedBy="user")
//	private List<Ticket> tickets;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
//	public List<Ticket> getTickets() {
//		return tickets;
//	}
//
//	public void setOpponents(List<Ticket> tickets) {
//		this.tickets = tickets;
//	}
}
