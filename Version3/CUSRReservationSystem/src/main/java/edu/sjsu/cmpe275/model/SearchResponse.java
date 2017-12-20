package edu.sjsu.cmpe275.model;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
	private String trainNumber;
	private char departingStation;
	private char arrivalStation;
	private String departingTime;
	private String arrivalTime;
	private String fare;
	//START : P-A
	private int passengerCount;
	private String departingDate1;
	private String departingDate2;
	private String connections;
	private String ticketType;
	//END : P-A
	private List<SearchResponse> connected;
	//START :P-A
	public String getConnections() {
		return connections;
	}
	public void setConnections(String connections) {
		this.connections = connections;
	}
	public int getPassengerCount() {
		return passengerCount;
	}
	public void setPassengerCount(int passengerCount) {
		this.passengerCount = passengerCount;
	}
	public String getDepartingDate1() {
		return departingDate1;
	}
	public void setDepartingDate1(String departingDate1) {
		this.departingDate1 = departingDate1;
	}
	public String getDepartingDate2() {
		return departingDate2;
	}
	public void setDepartingDate2(String departingDate2) {
		this.departingDate2 = departingDate2;
	}
	public String getTicketType() {
		return ticketType;
	}
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}
	//END :P-A
	public SearchResponse(){
		connected = new ArrayList<SearchResponse>();
	}
	public List<SearchResponse> getConnected() {
		return connected;
	}
	public void setConnected(List<SearchResponse> connected) {
		this.connected = connected;
	}
	public String getTrainNumber() {
		return trainNumber;
	}
	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	public char getDepartingStation() {
		return departingStation;
	}
	public void setDepartingStation(char departingStation) {
		this.departingStation = departingStation;
	}
	public char getArrivalStation() {
		return arrivalStation;
	}
	public void setArrivalStation(char arrivalStation) {
		this.arrivalStation = arrivalStation;
	}
	public String getDepartingTime() {
		return departingTime;
	}
	public void setDepartingTime(String departingTime) {
		this.departingTime = departingTime;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getFare() {
		return fare;
	}
	public void setFare(String fare) {
		this.fare = fare;
	}
}
