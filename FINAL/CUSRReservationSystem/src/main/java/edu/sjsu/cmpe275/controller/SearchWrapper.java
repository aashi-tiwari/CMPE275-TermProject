package edu.sjsu.cmpe275.controller;

import java.sql.Date;

/*
 * Wrapper class to store all search parameters
 */
public class SearchWrapper {
	//Is round trip
	public Boolean isRoundTrip=false;
	
	//Is exact time
	public Boolean isExactTime;
	
	//Exact time selected by user
	public Integer exactTime1;
	
	//Exact time selected by user for return trip
	public Integer exactTime2;
	
	//Ticket type
	public String ticketType;
	
	//Numbr of Passenger
	//public int passengerCount;
	
	//Departing station for 1 trip
	public Character departingStation1;
	//Arrival station for 1 trip
	public Character arrivalStation1;
	//Departing station for return trip
	public Character departingStation2;
	//Arrival station for return trip
	public Character arrivalStation2;
	//Departure date for 1 trip
	public String departingDate1;
	//Departure date for return trip
	public String departingDate2;
	// count of passenger on station
	//START: AASHI/PRNAJALI
	public int passengerCount;
	public String connections;

	public String userId; 
	//END: AASHI/PRNAJALI
	public SearchWrapper(){
		this.isExactTime=false;
	}
	//AASHI/PRANJALI -- added Passengercount to constructor
	public SearchWrapper(Boolean isRoundTrip,Character departingStation1,Character arrivalStation1, int passengerCount, String connections){
		this.isRoundTrip=isRoundTrip;
		this.departingStation1=departingStation1;
		this.arrivalStation1=arrivalStation1;
		this.passengerCount = passengerCount;
		this.connections = connections;
	}
}
