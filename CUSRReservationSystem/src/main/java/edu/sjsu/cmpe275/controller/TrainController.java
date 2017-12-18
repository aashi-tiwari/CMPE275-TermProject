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
import edu.sjsu.cmpe275.model.SearchResult;
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
			SearchResult result = getTrains(lst, s);
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
	private SearchResult getTrains(List<TrainCapacity> lst, SearchWrapper s) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JSONException {
		// TODO Auto-generated method stub
		SearchResult result = new SearchResult();
		if(s.connections.equals("None")){getNoneConnectionTrains(result, lst, s);}
		else if(s.connections.equals("One")){getOneConnectionTrains(result, lst, s, 0);}
		else getAnyConnectionTrains(result, lst, s);
		return result;
	}

	private void getAnyConnectionTrains(SearchResult result, List<TrainCapacity> lst, SearchWrapper s) {
		// TODO Auto-generated method stub
	}

	private void getOneConnectionTrains(SearchResult result, List<TrainCapacity> lst, SearchWrapper s, int currentConnections) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		System.out.println("SELECTING DIRECT TRAINS from station " + s.departingStation1 + " To station " + s.arrivalStation1);
		//List<TrainCapacity> result = new ArrayList<TrainCapacity>();
		int count = 0;
		int f = s.departingStation1 - 'A';
		int t = s.arrivalStation1 - 'A';
		TrainCapacity tc1;
		TrainCapacity tc2;
		List<Character> expressStations = new ArrayList<Character>();
		expressStations.add('F');
		expressStations.add('K');
		expressStations.add('P');
		expressStations.add('U');
		expressStations.add('Z');
		for(int i = 0; i< lst.size(); i++){
			tc1 = lst.get(i);
			char lastAccomodatedStation = canAccomodate(f, t, tc1, s);
			if(tc1.getTrainNumber().equals("SB0645")){
				System.out.println("Checking capacity in SB0645 and last accomodated station is " + lastAccomodatedStation);
			}
			if(lastAccomodatedStation == (char)('A' + (t-1))){
				//checking if that the train is express then it must be stopping at both the travelling stations
				if(tc1.getTrainNumber().endsWith("00")){
					if(!(expressStations.contains(s.departingStation1) && expressStations.contains(lastAccomodatedStation))){continue;}
				}
				SearchResponse response = new SearchResponse();
				response.setTrainNumber(tc1.getTrainNumber());
				response.setDepartingStation(s.departingStation1);
				response.setArrivalStation(s.arrivalStation1);
				response.setDepartingTime(getDepartingTime(tc1.getTrainNumber(), f, "D"));
				response.setArrivalTime(getDepartingTime(tc1.getTrainNumber(), t, "A"));
				response.setFare(String.valueOf("$" + (getFare(tc1.getTrainNumber(), f, t)+1)));
				result.getSearchResponse().add(response);
			}
			else{
				//Since this is intermediary station and passenger can only stop for max 2 hours, so we need to give him one amongst next 8 coming trains (as next 2 hours will have 8 coming trains)
				//if(s.departingStation1 == lastAccomodatedStation){continue;}
				if(tc1.getTrainNumber().endsWith("00")){
					if(!(expressStations.contains(s.departingStation1) && expressStations.contains(lastAccomodatedStation))){continue;}
				}
				//BIG EXPERIMENT
				//int nextTrainIndex = i;
				int nextTrainIndex = i+1;
				for(int j = nextTrainIndex; j<=8 ; j++){
					tc2 = lst.get(j);
					//checking if that the train is express then it must be stopping at both the travelling stations
					if(tc2.getTrainNumber().endsWith("00")){
						if(!(expressStations.contains(lastAccomodatedStation) && expressStations.contains(s.arrivalStation1))){continue;}
					}
					int currentStation = lastAccomodatedStation - 'A';
					char targetStation = canAccomodate(currentStation, t, tc2, s);
					//if we find one such train, we take our 1st train and the current connecting train and put them into response
					if(targetStation == (char)('A' + (t-1))){
					//	if(Integer.parseInt(getDepartingTime(tc1.getTrainNumber(), f)) > Integer.parseInt(getDepartingTime(tc2.getTrainNumber(), lastAccomodatedStation))){continue;}
						SearchResponse response = new SearchResponse();
						SearchResponse response1 = new SearchResponse();
						SearchResponse response2 = new SearchResponse();
						int firstTrainFare = getFare(tc1.getTrainNumber(), f, currentStation);
						int secondTrainFare = getFare(tc2.getTrainNumber(), currentStation, t);
						response.setFare(String.valueOf("$" + (firstTrainFare + secondTrainFare + 1)));
						response1.setTrainNumber(tc1.getTrainNumber());
						response2.setTrainNumber(tc2.getTrainNumber());
						response1.setDepartingStation(s.departingStation1);
						response2.setDepartingStation(lastAccomodatedStation);
						response1.setArrivalStation(lastAccomodatedStation);
						response2.setArrivalStation(s.arrivalStation1);
						response1.setDepartingTime(getDepartingTime(tc1.getTrainNumber(), f, "D"));
						response2.setDepartingTime(getDepartingTime(tc2.getTrainNumber(), currentStation, "D"));
						response1.setArrivalTime(getDepartingTime(tc1.trainNumber, currentStation, "A"));
						response2.setArrivalTime(getDepartingTime(tc2.trainNumber, t, "A"));
						response.getConnected().add(response1);
						response.getConnected().add(response2);
						result.getSearchResponse().add(response);
						break;
 					}
				}
			}
			if(++count>=5){break;}
		}
	}

	private char canAccomodate(int f, int t, TrainCapacity tc, SearchWrapper s) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		//checking for capacity in all stations between FROM and TO station
		if(tc.getTrainNumber().equals("SB0645")){
			System.out.println("Checking availability for SB0645");
		}
		char c = (char)('A' + f);
		for(int i = f; i<t ; i++){
			c = (char)('A' + i);
			String str = c + "_capacity";
			Field field = tc.getClass().getDeclaredField(str);
			int seatsLeft = ((Integer) field.get(tc)).intValue();
			if(tc.getTrainNumber().equals("SB0645")){
				System.out.println("Current station is " + c + " and current capacity is " + seatsLeft);
			}
			if(seatsLeft < s.passengerCount){
				//this is the last station that has the enough capacity
				return c;
			}
		}
		//this is the destination station
		return c;
	}

	public void getNoneConnectionTrains(SearchResult result, List<TrainCapacity> lst, SearchWrapper s) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JSONException{
		int count = 0;
		int f = s.departingStation1 - 'A';
		int t = s.arrivalStation1 - 'A';
		TrainCapacity tc;
		List<Character> expressStations = new ArrayList<Character>();
		//expressStations.add('A');
		expressStations.add('F');
		expressStations.add('K');
		expressStations.add('P');
		expressStations.add('U');
		expressStations.add('Z');
		for(int i = 0; i<lst.size(); i++){
			tc = lst.get(i);
			//if this is express train then TO and FROM both must be Express Stations
			if(tc.getTrainNumber().endsWith("00")){
				if(!(expressStations.contains(s.departingStation1) & expressStations.contains(s.arrivalStation1))){continue;}
			}
			char lastAccomodatedStation = canAccomodate(f, t, tc, s);
			if(lastAccomodatedStation == (char)('A' + (t-1))){
				SearchResponse response = new SearchResponse();
				response.setTrainNumber(tc.getTrainNumber());
				response.setDepartingStation(s.departingStation1);
				response.setArrivalStation(s.arrivalStation1);
				response.setDepartingTime(getDepartingTime(tc.getTrainNumber(), f, "D"));
				response.setArrivalTime(getDepartingTime(tc.getTrainNumber(), t, "A"));
				response.setFare("$" + (getFare(tc.getTrainNumber(), f, t)+1));
				result.getSearchResponse().add(response);
				count++;
			}
			if(count>=5){break;}
		}
	}
	private int getFare(String trainNumber, int f, int t) {
		// TODO Auto-generated method stub
		int start = f;
		int cost = 0;
		while(start <=t){
			start += 5;
			cost += 1;
		}
		return trainNumber.endsWith("00")?cost*2:cost;
	}

	private String getDepartingTime(String trainNumber, int f, String bound) {
		// TODO Auto-generated method stub
		if(trainNumber.endsWith("0600") && f == 0){return "06:00";} // if its the first station, no need to calculate any duration
		String type = trainNumber.endsWith("00")?"Express":"Regular";
		String departingTime = "";
		int timeTravelled = 0;
		if(type.equals("Express")){
			
			int start = 1;
			while(start++ <= f){
				timeTravelled +=5; // duration to travel to next station
				if(start%5 == 0){timeTravelled +=3;}  // duration of stopping at that station
			}
		}else{
			timeTravelled = (f - 0)*8;
		}
		if(bound.equals("A")){timeTravelled -=3;}
		int startingTimeInMinutes = Integer.parseInt(trainNumber.substring(2));
		int departingTimeinMinutes = ((startingTimeInMinutes/100)*60 + (startingTimeInMinutes%100) + timeTravelled);
		String hours = (departingTimeinMinutes/60<10)?("0" + departingTimeinMinutes/60):(String.valueOf(departingTimeinMinutes/60));
		String minutes =  (departingTimeinMinutes%60<10)?("0" + departingTimeinMinutes%60):(String.valueOf(departingTimeinMinutes%60));
		return (hours + ":" + minutes);
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
