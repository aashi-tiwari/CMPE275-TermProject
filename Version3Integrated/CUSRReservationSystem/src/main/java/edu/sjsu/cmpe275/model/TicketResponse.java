package edu.sjsu.cmpe275.model;

import java.util.ArrayList;
import java.util.List;

public class TicketResponse {
	List<Ticket> searchResponse;
	List<Ticket> returnResponse;
	public List<Ticket> getReturnResponse() {
		return returnResponse;
	}
	public void setReturnResponse(List<Ticket> returnResponse) {
		this.returnResponse = returnResponse;
	}
	public List<Ticket> getSearchResponse() {
		return searchResponse;
	}
	public void setSearchResponse(List<Ticket> ticketResponse) {
		this.searchResponse = ticketResponse;
	}
	public TicketResponse(){
		searchResponse = new ArrayList<Ticket>();
		returnResponse = new ArrayList<Ticket>();
	}
}
