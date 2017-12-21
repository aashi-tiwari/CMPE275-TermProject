package edu.sjsu.cmpe275.controller;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping(value = "/trains/systemReset", method = RequestMethod.POST)
    public void updateSystem(@RequestBody String payload) {
    	try{
    		JSONObject jsonObj = new JSONObject(payload.toString());
    		if(jsonObj.has("defaultValue")==true){
    			int default_capacity = Integer.parseInt(jsonObj.getString("defaultValue"));
    			
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
