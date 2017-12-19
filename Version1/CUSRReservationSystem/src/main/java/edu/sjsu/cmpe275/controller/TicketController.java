package edu.sjsu.cmpe275.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.model.Response;
import edu.sjsu.cmpe275.model.Ticket;
import edu.sjsu.cmpe275.repository.TicketRepository;

@RestController 
@RequestMapping("/CUSR")
public class TicketController {
	@Autowired
	private TicketRepository ticketRepository; 
	
	// Create Ticket
	@RequestMapping(value = "/ticket", method = RequestMethod.POST)
    public ResponseEntity<?> createTicket(@RequestBody String payload) {
		try{
			System.out.println("************in createTicket");
			JSONObject jsonObj = new JSONObject(payload.toString());
			//JsonObj.has("searchResponse")==true ? jsonObj.getBoolean("isRoundTrip"):false;
	        return new ResponseEntity<>(null, HttpStatus.OK);
    	}
    	catch(Exception e){
    		System.out.println("exception="+e);
    		return new ResponseEntity<>("BadRequest",HttpStatus.BAD_REQUEST);
    	}
    }
}
