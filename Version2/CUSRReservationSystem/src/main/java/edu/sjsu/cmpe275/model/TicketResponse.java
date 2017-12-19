package edu.sjsu.cmpe275.model;

import java.util.ArrayList;
import java.util.List;

public class TicketResponse {
	List<Ticket> ticketResponse;
	public List<Ticket> getTicketResponse() {
		return ticketResponse;
	}
	public void setTicketResponse(List<Ticket> ticketResponse) {
		this.ticketResponse = ticketResponse;
	}
	public TicketResponse(){
		ticketResponse = new ArrayList<Ticket>();
	}
}
