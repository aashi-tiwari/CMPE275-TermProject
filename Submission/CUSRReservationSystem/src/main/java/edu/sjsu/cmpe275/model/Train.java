package edu.sjsu.cmpe275.model;

import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Train {
	
	@Id
	private String trainNumber;
	
	private String type;
	
	private String direction;
	
	private int capacity;
	
	private int startTime;
	
	public Train(){
	}
	
	public Train(String trainNumber,String type,String direction,int capacity,int startTime){
		this.trainNumber=trainNumber;
		this.type=type;
		this.direction=direction;
		this.capacity=capacity;
		this.startTime=startTime;
    }

	public String getTrainNumber() {
		return trainNumber;
	}

	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
}
