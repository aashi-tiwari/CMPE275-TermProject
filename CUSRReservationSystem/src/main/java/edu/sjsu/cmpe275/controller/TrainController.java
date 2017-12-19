package edu.sjsu.cmpe275.controller;

import java.lang.reflect.Field;
import java.time.LocalDate;

import java.util.*;

import javax.naming.directory.InvalidAttributeIdentifierException;
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
    //START: PRANJALI/AASHI
	//helper method to select top 5 trains
	private SearchResult getTrains(List<TrainCapacity> lst, SearchWrapper s) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JSONException {
		// TODO Auto-generated method stub
		SearchResult result = new SearchResult();
		if(s.connections.equals("None")){getNoneConnectionTrains(result, lst, s);}
		else if(s.connections.equals("One")){getOneConnectionTrains(result, lst, s);}
		else getAnyConnectionTrains(result, lst, s);
		return result;
	}
	
	private void getAnyConnectionTrains(SearchResult result, List<TrainCapacity> lst, SearchWrapper s) {
		// TODO Auto-generated method stub
		
	}

	private void getOneConnectionTrains(SearchResult result, List<TrainCapacity> lst, SearchWrapper s) {
		// TODO Auto-generated method stub
		System.out.println("*******************GETOneConnectionTrainSTART*******************");
		System.out.println("Getting One connection trains");
		int from = s.departingStation1 - 'A';
		int to = s.arrivalStation1 - 'A';
		TrainCapacity tc1;
		TrainCapacity tc2;
		int count = 0;
		for(int i = 0; i<lst.size();i++){
			tc1 = lst.get(i);
			int lastDroppingStation = getLastDroppingStation(tc1, from, to, s);
			System.out.println("Current train is  " + tc1.getTrainNumber() + " and can drop till station " + lastDroppingStation);
			//cannot take this train
			if(lastDroppingStation == -1){continue;}
			//this is a direct connection found
			else if(lastDroppingStation == to){
				//check if exrpress train drops and picks up these locations
				if(tc1.getTrainNumber().endsWith("00")){
					if(!canExpressBeTaken(s.departingStation1, s.arrivalStation1)){continue;}
				}
				System.out.println("So can travel with this train");
				SearchResponse response = createResponseObject(tc1.trainNumber, s.departingStation1, s.arrivalStation1);
				result.getSearchResponse().add(response);
				count++;
			}else{
				if(tc1.getTrainNumber().endsWith("00")){
					if(!canExpressBeTaken(s.departingStation1, (char)(lastDroppingStation + 'A'))){continue;}
				}
				System.out.println("Intermediate Station " + (char)('A' + lastDroppingStation));
				int nextTrainIndex = i+1;
				//size j<=8 because passenger can only weight for 2 hours at a station
				for(int j = nextTrainIndex; j<=8; j++){
					//case to check if there is no more next train
					if(j >= lst.size()){break;}
					tc2 = lst.get(j);
					// if its an express train then it should have stops at boarding and dropping stations
					if(tc2.getTrainNumber().endsWith("00")){
						if(!canExpressBeTaken((char)(lastDroppingStation + 'A'), s.arrivalStation1)){continue;}
					}
					int currentStation = lastDroppingStation;
					int finalDroppingStation = getLastDroppingStation(tc2, currentStation, to, s);
					if(finalDroppingStation == to){
						SearchResponse response = new SearchResponse();
						SearchResponse response1 = createResponseObject(tc1.trainNumber, s.departingStation1, (char)(lastDroppingStation + 'A'));
						SearchResponse response2 = createResponseObject(tc2.trainNumber, (char)(lastDroppingStation + 'A'), s.arrivalStation1);
						int fare1 = getFare(tc1.getTrainNumber(), s.departingStation1, (char)(lastDroppingStation + 'A'));
						int fare2 = getFare(tc2.getTrainNumber(), (char)(lastDroppingStation + 'A'), s.arrivalStation1);
						String totalFare = "$" + (fare1 + fare2 + 1);
						response.setFare(totalFare);
						response.getConnected().add(response1);
						response.getConnected().add(response2);
						result.getSearchResponse().add(response);
						count++;
					}
				}
			}
			if(count>=5){break;}
		}
		System.out.println("*******************GETOneConnectionTrainEND*******************");
	}

	private void getNoneConnectionTrains(SearchResult result, List<TrainCapacity> lst, SearchWrapper s) {
		// TODO Auto-generated method stub
		System.out.println("Getting none connection trains");
		int from = s.departingStation1 - 'A';
		int to = s.arrivalStation1 - 'A';
		TrainCapacity tc;
		int count = 0;
		System.out.println("From " + from + " to " + to);
		for(int i = 0; i<lst.size(); i++){
			tc = lst.get(i);
			// If this is express train then make sure it stops both at source and destination to consider it for travel
			if(tc.getTrainNumber().endsWith("00")){
				if(!canExpressBeTaken(s.departingStation1, s.arrivalStation1)){continue;}
			}
			int lastDroppingStation = getLastDroppingStation(tc, from, to, s);
			System.out.println("Current train is  " + tc.getTrainNumber() + " and can drop till station " + lastDroppingStation);
			if(lastDroppingStation == to){
				System.out.println("So can travel with this train");
				SearchResponse response = createResponseObject(tc.trainNumber, s.departingStation1, s.arrivalStation1);
				result.getSearchResponse().add(response);
				count++;
				
			}
			if(count>=5){break;}
		}
	}
	
	private boolean canExpressBeTaken(Character departingStation, Character arrivalStation) {
		// TODO Auto-generated method stub
		List<Character> expressTrainStations = new ArrayList<Character>();
		//F, K, P, U, and Z
		expressTrainStations.add('A');
		expressTrainStations.add('F');
		expressTrainStations.add('K');
		expressTrainStations.add('P');
		expressTrainStations.add('U');
		expressTrainStations.add('Z');
		if(expressTrainStations.contains(departingStation) && expressTrainStations.contains(arrivalStation)){return true;}
		return false;
	}

	private SearchResponse createResponseObject(String trainNumber, Character departingStation, Character arrivalStation) {
		// TODO Auto-generated method stub
		SearchResponse response = new SearchResponse();
		response.setTrainNumber(trainNumber);
		response.setDepartingStation(departingStation);
		response.setArrivalStation(arrivalStation);
		response.setDepartingTime(getTime(trainNumber, departingStation, "D"));
		response.setArrivalTime(getTime(trainNumber, arrivalStation, "A"));
		response.setFare("$" + (getFare(trainNumber, departingStation, arrivalStation) + 1));
		return response;
	}
	
	private int getFare(String trainNumber, Character departingStation, Character arrivalStation) {
		int start = departingStation - 'A';
		int t = arrivalStation - 'A';
		int cost = (t - start)/5;
		if((t-start)%5 != 0){cost++;}
		System.out.println("Cost calculated is **************** " + cost + " plus $1 for transaction on whole");
		return trainNumber.endsWith("00")?cost*2:cost;
	}

	//Getting departing or arrival time
	private String getTime(String trainNumber, Character departingStation, String bound) {
		int f = departingStation - 'A';
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

	private int getLastDroppingStation(TrainCapacity tc, int from, int to, SearchWrapper s) {
		// TODO Auto-generated method stub
		System.out.println("Finding capacity for " + tc.getTrainNumber());
		int i = from;
		for(; i<=to; i++){
			char currentStation = (char)('A' + i);
			String str = currentStation + "_capacity";
			try {
				Field field = tc.getClass().getDeclaredField(str);
				int currentCapacity = ((Integer) field.get(tc)).intValue();
				System.out.println("For station " + currentStation + " Current capacity is " + currentCapacity);
				if(currentCapacity < s.passengerCount){
					if(i == from){
						System.out.println("Cannot travel from this train as boarding and dropping stations are same");
						return -1;
					}else{
						System.out.println("This is the dropping station");
						return i;
					}
				}
			} catch (NoSuchFieldException e) {
				//Field field = tc.getClass().getDeclaredField(str);
				throw new IllegalArgumentException("NO FIELD AS " + str + " EXISTS");
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				//int currentCapacity = ((Integer) field.get(tc)).intValue();
				throw new IllegalArgumentException("NO SUCH TRAIN EXISTS");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Can drop at destination " + (i-1));
		//since i has increaed by above i++
		return i-1;
	}
//END: PRNAJALI/AASHI
	
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
