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
	private List<SearchResponse> connected;
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
