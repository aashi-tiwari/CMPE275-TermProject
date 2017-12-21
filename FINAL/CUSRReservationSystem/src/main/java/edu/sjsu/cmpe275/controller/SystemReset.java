package edu.sjsu.cmpe275.controller;


import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.repository.TicketRepository;
import edu.sjsu.cmpe275.repository.TrainCapacityRepository;
import edu.sjsu.cmpe275.repository.TrainRepository;

@RestController
@RequestMapping("/CUSR")
public class SystemReset {
	
	@Autowired
	private TrainRepository trainRepository;
	@Autowired
	private TrainCapacityRepository trainCapacityRepository;
	@Autowired
	private TicketRepository ticketRepository;
	
	@CrossOrigin
	@RequestMapping(value = "/trains/systemReset", method = RequestMethod.POST)
    public void updateSystem(@RequestBody String defaultValue) {
    	try{
    		System.out.println("Catching jaosn");
//    		JSONObject jsonObj = new JSONObject(payload.toString());
//    		System.out.println("CATCHED");
    		if(defaultValue!=null){
    			int default_capacity = Integer.parseInt(defaultValue);
    			System.out.println("DEFAULT " + default_capacity);
    			// Deleting all tickets
        		ticketRepository.deleteAll(); 
        		
        		// Updating all train capacities
    	    	System.out.println("updateReset");
    	    	trainCapacityRepository.updateTrainCapacity(default_capacity);		
    		}	 
    	}
    	catch(Exception e){
    		System.out.println("error catched");
    	}
    }
}

