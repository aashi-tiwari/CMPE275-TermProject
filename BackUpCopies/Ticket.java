package edu.sjsu.cmpe275.model;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Ticket {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	//Transaction Id generated in POST is populated here
	private long transactionId;
	
	private String trainNumber;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="userId")
	private User user;
	
	//Start changes: P-S
	//Additional variables stored in order to store original search criteria
	private int passengerCount;
	private String connections;
	private String ticketType;
	//End changes: P-S

	private Character departingStation;
	
	private Character arrivalStation;
	
	private Date travelDate;
	
	private Time departureTime;
	
	private Time arrivalTime;
	
	private int fare;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getTrainNumber() {
		return trainNumber;
	}

	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	
	public Character getDepartingStation() {
		return departingStation;
	}

	public void setDepartingStation(Character departingStation) {
		this.departingStation = departingStation;
	}

	public Character getArrivalStation() {
		return arrivalStation;
	}

	public void setArrivalStation(Character arrivalStation) {
		this.arrivalStation = arrivalStation;
	}

	public Date getTravelDate() {
		return travelDate;
	}

	public void setTravelDate(Date travelDate) {
		this.travelDate = travelDate;
	}

	public Time getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Time departureTime) {
		this.departureTime = departureTime;
	}

	public Time getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Time arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getFare() {
		return fare;
	}

	public void setFare(int fare) {
		this.fare = fare;
	}
	//Start: P-S
	public int getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(int passengerCount) {
		this.passengerCount = passengerCount;
	}

	public String getConnections() {
		return connections;
	}

	public void setConnections(String connections) {
		this.connections = connections;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	//End: P-S

}
