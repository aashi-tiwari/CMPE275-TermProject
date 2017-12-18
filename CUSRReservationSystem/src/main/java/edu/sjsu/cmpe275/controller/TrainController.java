package edu.sjsu.cmpe275.controller;

import java.lang.reflect.Field;
import java.time.LocalDate;

import java.util.*;

import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.model.Response;
import edu.sjsu.cmpe275.model.SearchResponse;
import edu.sjsu.cmpe275.model.Train;
import edu.sjsu.cmpe275.model.TrainCapacity;
import edu.sjsu.cmpe275.repository.TrainCapacityRepository;
import edu.sjsu.cmpe275.repository.TrainRepository;




@RestController
@RequestMapping("/CUSR")
public class TrainController {
	
	@Autowired
	private TrainRepository trainRepository;
	@Autowired
	private TrainCapacityRepository trainCapacityRepository;
	
	// Get a Single Train
	@RequestMapping(value = "/trains/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getTrain(@Valid @PathVariable String id) {
    	try{
	    	Train t = trainRepository.findOne(id);
	    	if (t == null) {
	            return new ResponseEntity<>(new Response(404, "Sorry, the requested train with number "
	                    + id + " does not exist"), HttpStatus.NOT_FOUND);

	        } 
	    	return new ResponseEntity<>(t,HttpStatus.OK);
    	}
    	catch(Exception e){
    		return new ResponseEntity<>("BadRequest",HttpStatus.BAD_REQUEST);
    	}
        
    }
	
	// Get All Trains
	@RequestMapping(value = "/trains", method = RequestMethod.GET)
    public ResponseEntity<?> getAllTrains() {
    	return new ResponseEntity<>(trainRepository.findAll(), HttpStatus.OK);
    }
	
	/*
	 *  Search Train 
	 *  Search Params: isReturnTrip,From, To, TravelDate
	 */
	@RequestMapping(value = "/trains", method = RequestMethod.POST)
    public ResponseEntity<?> getTrains(@RequestBody String payload) {
		try{
			System.out.println("************in getTrain "+payload);
			JSONObject jsonObj = new JSONObject(payload.toString());
			//Create Search Wrapper for all search parameters requested
			SearchWrapper s= new SearchWrapper();
			s.isRoundTrip=jsonObj.has("isRoundTrip")==true ? jsonObj.getBoolean("isRoundTrip"):false;
			s.isExactTime=jsonObj.has("isExactTime")==true ? jsonObj.getBoolean("isExactTime"):false;
			if(s.isExactTime){
				String exactTimeStr=jsonObj.getString("exactTime");
				exactTimeStr=exactTimeStr.replaceAll(":", "");
				System.out.println("************in getTrain exactTimeStr "+exactTimeStr);
				s.exactTime=Integer.parseInt(exactTimeStr);
				System.out.println("************in getTrain s.exactTime "+s.exactTime);
			}
			s.departingStation1 = jsonObj.getString("departingStation1").charAt(0);
			s.arrivalStation1 = jsonObj.getString("arrivalStation1").charAt(0);
			s.departingDate1 = jsonObj.getString("departingDate1");
			//AASHI/PRANJALI
			s.passengerCount = jsonObj.getInt("passengerCount");
			s.connections = jsonObj.getString("connections");
			//AASHI/PRANJALI
		    System.out.println("PASSENGERS " + s.passengerCount);
			//Check SB or NB 
			String direction;
			if(s.departingStation1 < s.arrivalStation1)
				direction="SB";
			else
				direction="NB";
			
			System.out.println("************in getTrain direction="+direction);
			List<TrainCapacity> lst = new ArrayList<TrainCapacity>();
			if(s.isExactTime){
				//Calculate current time in int
				Calendar cal = Calendar.getInstance();
				int hours = cal.get(Calendar.HOUR_OF_DAY);
				int min = cal.get(Calendar.MINUTE);
				System.out.println("************in getTrain hours= "+hours+" "+min);
				int currentTime=0;
				//lst=trainCapacityRepository.searchTrainsByExactTime(direction,s.departingDate1,s.exactTime,currentTime);
			}
			else
				lst=trainCapacityRepository.searchTrains(direction,s.departingDate1);
			
			System.out.println("************in getTrain lst.size= "+lst.size());
    		//START: AASHI/PRANJALI
			  List<SearchResponse> result = getTrains(lst, s);
	          return new ResponseEntity<>(result, HttpStatus.OK);
	       //END: AASHI/PRNAJALI
    	}
    	catch(Exception e){
    		System.out.println("exception="+e);
    		return new ResponseEntity<>("BadRequest",HttpStatus.BAD_REQUEST);
    	}
}
    //START: AASHI/PRANJALI
	//helper method to select top 5 trains
	private List<SearchResponse> getTrains(List<TrainCapacity> lst, SearchWrapper s) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JSONException {
		// TODO Auto-generated method stub
		List<SearchResponse> result = new ArrayList<SearchResponse>();
		
		if(s.connections.equals("None")){getNoneConnectionTrains(result, lst, s);}
		else if(s.connections.equals("One")){getOneConnectionTrains(result, lst, s, 0);}
		else getAnyConnectionTrains(result, lst, s);
		return result;
	}

	private void getAnyConnectionTrains(List<SearchResponse> result, List<TrainCapacity> lst, SearchWrapper s) {
		// TODO Auto-generated method stub
	}

	private void getOneConnectionTrains(List<SearchResponse> result, List<TrainCapacity> lst, SearchWrapper s, int currentConnections) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		System.out.println("SELECTING DIRECT TRAINS from station " + s.departingStation1 + " To station " + s.arrivalStation1);
//		//List<TrainCapacity> result = new ArrayList<TrainCapacity>();
//		int count = 0;
//		int f = s.departingStation1 - 'A';
//		int t = s.arrivalStation1 - 'A';
//		TrainCapacity one;
//		TrainCapacity two;
//		TrainCapacity tc;
//		for(int i = 0; i< lst.size(); i++){
//			tc = lst.get(i);
//			char lastAccomodatedStation = canAccomodate(f, t, tc, s);
//			if(lastAccomodatedStation == (char)('A' + (t-1))){
//				result.add(tc);
//				count++;
//			}
//			else if(currentConnections <1){
//				currentConnections++;
//				one = tc;
//				System.out.println("Connecting train could be " + tc.getTrainNumber());
//			}
//			if(count>=5){break;}
//		}
	}

	private char canAccomodate(int f, int t, TrainCapacity tc, SearchWrapper s) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		//checking for capacity in all stations between FROM and TO station
		char c = (char)('A' + f);
		for(int i = f; i<t ; i++){
			c = (char)('A' + i);
			String str = c + "_capacity";
			Field field = tc.getClass().getDeclaredField(str);
			int seatsLeft = ((Integer) field.get(tc)).intValue();
			if(seatsLeft < s.passengerCount){
				//this is the last station that has the enough capacity
				return c;
			}
		}
		//this is the destination station
		return c;
	}

	public void getNoneConnectionTrains(List<SearchResponse> result, List<TrainCapacity> lst, SearchWrapper s) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JSONException{
		int count = 0;
		int f = s.departingStation1 - 'A';
		int t = s.arrivalStation1 - 'A';
		//System.out.println("FROM IS " + f + " DESTINATION IS " + t + " Passenger wants " + s.passengerCount + " seats");
		for(TrainCapacity tc : lst){
			char lastAccomodatedStation = canAccomodate(f, t, tc, s);
			if(lastAccomodatedStation == (char)('A' + (t-1))){
				SearchResponse response = new SearchResponse();
				response.setTrainNumber(tc.getTrainNumber());
				response.setDepartingStation(s.departingStation1);
				response.setArrivalStation(s.arrivalStation1);
				result.add(response);
				count++;
			}
			if(count>=5){break;}
		}
	}
//END: AASHI/PRNAJALI
	
//	// Create Train
//	@RequestMapping(value = "/trains", method = RequestMethod.POST)
//    public ResponseEntity<?> createTrain(@RequestBody Train input) {
//		try{
//			System.out.println("************in createTrain");
//    		if(input.getTrainNumber()==null)
//    			return new ResponseEntity<>("Missing required parameters",HttpStatus.BAD_REQUEST);
//    		System.out.println("time="+input.getStartTime());
//    		Train t= new Train(input.getTrainNumber(),input.getType(),input.getDirection(),input.getCapacity(),input.getStartTime(),input.getStatus());
//	        t=trainRepository.save(t);
//	        System.out.println("created@@@@@@2=");
//	        return new ResponseEntity<>(t, HttpStatus.OK);
//    	}
//    	catch(Exception e){
//    		System.out.println("exception="+e);
//    		return new ResponseEntity<>("BadRequest",HttpStatus.BAD_REQUEST);
//    	}
//    }
    
	// Put request to cancel train
	//add date
    @RequestMapping(value = "/trains/canceltrain/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTrainStatus(@PathVariable ("id")String id) {
		try{
			Train t = trainRepository.findOne(id);
			if(t == null) {
				return new ResponseEntity<>("BadRequest",HttpStatus.NOT_FOUND);
			}
			t.setStatus("Cancelled");
			Train updatedTrain=trainRepository.save(t);
			return new ResponseEntity<>(updatedTrain,HttpStatus.OK);
		}
		catch(Exception e){
			return new ResponseEntity<>("BadRequest",HttpStatus.NOT_FOUND);
		}
	}
}
