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
import org.springframework.web.bind.annotation.CrossOrigin;
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
import edu.sjsu.cmpe275.repository.TicketRepository;
import edu.sjsu.cmpe275.repository.TrainCapacityRepository;
import edu.sjsu.cmpe275.repository.TrainRepository;

@RestController
@RequestMapping("/CUSR")
public class TrainController {
	
	@Autowired
	private TrainRepository trainRepository;
	@Autowired
	private TrainCapacityRepository trainCapacityRepository;
	@Autowired
	private TicketRepository ticketRepository;
	
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
	@CrossOrigin
	@RequestMapping(value = "/trains", method = RequestMethod.POST)
    public ResponseEntity<?> getTrains(@RequestBody String payload) {
		try{
	//		System.out.println("************in getTrain "+payload);
			JSONObject jsonObj = new JSONObject(payload.toString());
			//Create Search Wrapper for all search parameters requested
			SearchWrapper s= new SearchWrapper();
			s.isRoundTrip=jsonObj.has("isRoundTrip")==true ? jsonObj.getBoolean("isRoundTrip"):false;
			s.isExactTime=jsonObj.has("isExactTime")==true ? jsonObj.getBoolean("isExactTime"):false;
			s.ticketType=jsonObj.has("ticketType")==true ? jsonObj.getString("ticketType") : "Any";
			if((jsonObj.has("exactTime1")  && !jsonObj.getString("exactTime1").equals("")) ||(jsonObj.has("exactTime2") && !jsonObj.getString("exactTime2").equals(""))){
				s.isExactTime=true;
				//If exact time specified, convert the exact time selected into the time format in Train_Capacity table
				if(jsonObj.has("exactTime1")  && !jsonObj.getString("exactTime1").equals("")){
					String exactTimeStr=jsonObj.getString("exactTime1");
					exactTimeStr=exactTimeStr.replaceAll(":", "");
					s.exactTime1=Integer.parseInt(exactTimeStr);
				}
				if(jsonObj.has("exactTime2")  && !jsonObj.getString("exactTime2").equals("")){
					String exactTimeStr=jsonObj.getString("exactTime2");
					exactTimeStr=exactTimeStr.replaceAll(":", "");
					s.exactTime2=Integer.parseInt(exactTimeStr);
				}
				
			}
			s.departingStation1 = jsonObj.getString("departingStation1").charAt(0);
			s.arrivalStation1 = jsonObj.getString("arrivalStation1").charAt(0);
			s.departingDate1 = jsonObj.getString("departingDate1").split("T")[0];
			s.departingDate2 = jsonObj.has("departingDate2")==true ? jsonObj.getString("departingDate2"):"";
			//AASHI/PRANJALI
			s.passengerCount = jsonObj.has("passengerCount")==true ?jsonObj.getInt("passengerCount"):1;
			s.connections = jsonObj.has("connections")==true ? jsonObj.getString("connections"):"";
			//AASHI/PRANJALI
	//	    System.out.println("PASSENGERS " + s.passengerCount);
			//Check SB or NB 
			String direction,returnDirection;
			if(s.departingStation1 < s.arrivalStation1){
				direction="SB";
				returnDirection="NB";
			}
			else{
				direction="NB";
				returnDirection="SB";
			}
			
			List<TrainCapacity> onewayLst = new ArrayList<TrainCapacity>();
			List<TrainCapacity> returnLst = new ArrayList<TrainCapacity>();
			List<String> test = new ArrayList<String>();
			TrainCapacityHelper helper= new TrainCapacityHelper();
			Map<String,List<TrainCapacity>> mapStringToLstCapacity=helper.queryTrainCapacityRecords(s,direction,returnDirection,trainCapacityRepository);
			onewayLst=mapStringToLstCapacity.get("oneway");
			returnLst=mapStringToLstCapacity.get("return");
			
			//START: AASHI/PRANJALI
			SearchResult result = getTrains(onewayLst, returnLst, s, direction);
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
	public SearchResult getTrains(List<TrainCapacity> lst, List<TrainCapacity> returnLst, SearchWrapper s, String direction) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JSONException {
		// TODO Auto-generated method stub
		SearchResult result = new SearchResult();
		SearchResult upJourney = new SearchResult();
		SearchResult downJourney = new SearchResult();
		if(s.isRoundTrip == false){
			if(s.connections.equals("None")){getNoneConnectionTrains(result, lst, s, direction, false);}
			//else if(s.connections.equals("One")){getOneConnectionTrains(result, lst, s, direction, false);}
			else{getOneConnectionTrains(result, lst, s, direction, false);}
		}else{
			//upjourney
			if(s.connections.equals("None")){getNoneConnectionTrains(result, lst, s, direction, false);}
			//else if(s.connections.equals("One")){getOneConnectionTrains(result, lst, s, direction, false);}
			else{getOneConnectionTrains(result, lst, s, direction, false);}
			//downjourney
			if(s.connections.equals("None")){getNoneConnectionTrains(result, returnLst, s, direction, true);}
			//else if(s.connections.equals("One")){getOneConnectionTrains(result, returnLst, s, direction, true);}
			else{getOneConnectionTrains(result, returnLst, s, direction, true);}
		}
		return result;
	}
	private int getLastDroppingStationNB(TrainCapacity tc, int from, int to, SearchWrapper s) {
			// TODO Auto-generated method stub
		int i = from;
		for(; i>=to; i--){
			char currentStation = (char)('A' + i);
			String str = currentStation + "_capacity";
			try {
				Field field = tc.getClass().getDeclaredField(str);
				int currentCapacity = ((Integer) field.get(tc)).intValue();
				if(currentCapacity < s.passengerCount){
					if(i == from){
						return -1;
					}else{
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
		return i+1;
	}
	private void getOneConnectionTrains(SearchResult result, List<TrainCapacity> lst, SearchWrapper s, String direction, boolean isReturnJourney) {
		// TODO Auto-generated method stub
		int from = s.departingStation1 - 'A';
		int to = s.arrivalStation1 - 'A';
		TrainCapacity tc1;
		TrainCapacity tc2;
		int countFirst = 0;
		int countSecond = 0;
		for(int i = 0; i<lst.size();i++){
			tc1 = lst.get(i);
			int lastDroppingStation = 0;
			//*********
			int destination = 0;
			if(!isReturnJourney){
				if(direction.equals("SB")){lastDroppingStation = getLastDroppingStationSB(tc1, from, to, s);}
				else if(direction.equals("NB")){lastDroppingStation = getLastDroppingStationNB(tc1, from, to, s);}
				destination = to;
			}else if(isReturnJourney){
				destination = from;
				if(direction.equals("SB")){lastDroppingStation = getLastDroppingStationNB(tc1, to, from, s);}
				else if(direction.equals("NB")){lastDroppingStation = getLastDroppingStationSB(tc1, to, from, s);}
			}
			
			//*********
			if(lastDroppingStation == -1){continue;}
			else if(lastDroppingStation == destination){
				//check if exrpress train drops and picks up these locations
				if(tc1.getTrainNumber().endsWith("00")){
					if(!canExpressBeTaken(s.departingStation1, s.arrivalStation1)){continue;}
				}
	//			System.out.println("So can travel with this train");
				SearchResponse response = new SearchResponse();
				if(!isReturnJourney && countFirst < 5){
					response = createResponseObject(tc1.trainNumber, s.departingStation1, s.arrivalStation1, s.departingDate1, s);
					result.getSearchResponse().add(response);
					countFirst++;
					System.out.println("CountFirst " + countFirst + " with train number  " + tc1.trainNumber);
				}else if(isReturnJourney && countSecond < 5){
					response = createResponseObject(tc1.trainNumber, s.arrivalStation1, s.departingStation1,  s.departingDate1, s);
					result.getSearchResponse().add(response);
					result.getReturnResponse().add(response);
					countSecond++;
					System.out.println("CountSecond " + countSecond + " with train number  " + tc1.trainNumber);
				}
			}
			else{
				if(tc1.getTrainNumber().endsWith("00")){
					if(!canExpressBeTaken(s.departingStation1, (char)(lastDroppingStation + 'A'))){continue;}
				}
				int nextTrainIndex = i+1;
				for(int j = nextTrainIndex; j<=8; j++){
					if(j >= lst.size()){break;}
					tc2 = lst.get(j);
					int currentStation = lastDroppingStation;
					int finalDroppingStation = 0;
					if(!isReturnJourney){
						if(direction.equals("SB")){finalDroppingStation = getLastDroppingStationSB(tc2, currentStation, to, s);}
						else if(direction.equals("NB")){finalDroppingStation = getLastDroppingStationNB(tc2, currentStation, to, s);}
						destination = to;
					}else if(isReturnJourney){
						destination = currentStation;
						if(direction.equals("SB")){finalDroppingStation = getLastDroppingStationNB(tc2, to, currentStation, s);}
						else if(direction.equals("NB")){finalDroppingStation = getLastDroppingStationSB(tc2, to, currentStation, s);}
					}
//					if(direction.equals("SB")){finalDroppingStation = getLastDroppingStationSB(tc2, currentStation, to, s);}
//					else if(direction.equals("NB")){finalDroppingStation = getLastDroppingStationNB(tc2, currentStation, to, s);}
					//int finalDroppingStation = getLastDroppingStationSB(tc2, currentStation, to, s);
					if(finalDroppingStation == destination){
						if(tc2.getTrainNumber().endsWith("00")){
							if(!canExpressBeTaken((char)(lastDroppingStation + 'A'), ((char)(destination + 'A')))){continue;}
						}
						SearchResponse response = new SearchResponse();
						SearchResponse response1 = new SearchResponse();
						SearchResponse response2 = new SearchResponse();
						if(!isReturnJourney && countFirst <5){
							response1 = createResponseObject(tc1.trainNumber, s.departingStation1, (char)(currentStation + 'A'), s.departingDate1, s);
							response2 = createResponseObject(tc2.trainNumber, (char)(currentStation + 'A'), s.arrivalStation1, s.departingDate1, s);
							int fare1 = getFare(tc1.getTrainNumber(), s.departingStation1, (char)(currentStation + 'A'));
							int fare2 = getFare(tc2.getTrainNumber(), (char)(currentStation + 'A'), s.arrivalStation1);
							String totalFare = "$" + (fare1 + fare2 + 1);
							response.setFare(totalFare);
							response.setPassengerCount(s.passengerCount);
							response.setDepartingDate1(s.departingDate1);
							response.getConnected().add(response1);
							response.getConnected().add(response2);
							result.getSearchResponse().add(response);
							countFirst++;
						}else if(isReturnJourney && countSecond < 5){
							response1 = createResponseObject(tc2.trainNumber,  s.arrivalStation1, (char)(lastDroppingStation + 'A'), s.departingDate2, s);
							response2 = createResponseObject(tc1.trainNumber,  (char)(lastDroppingStation + 'A'), s.departingStation1, s.departingDate2, s);
							int fare1 = getFare(tc1.getTrainNumber(), s.departingStation1, (char)(lastDroppingStation + 'A'));
							int fare2 = getFare(tc2.getTrainNumber(), (char)(lastDroppingStation + 'A'), s.arrivalStation1);
							String totalFare = "$" + (fare1 + fare2 + 1);
							response.setFare(totalFare);
							response.setPassengerCount(s.passengerCount);
							response.setDepartingDate1(s.departingDate1);
							response.getConnected().add(response1);
							response.getConnected().add(response2);
							//result.getSearchResponse().add(response);
							result.getReturnResponse().add(response);
							countSecond++;
							
						}
					}
				}
			}
			if(countFirst>=5 && countSecond>=5){break;}
		}
	//	System.out.println("*******************GETOneConnectionTrainEND*******************");
	}
	

	private void getNoneConnectionTrains(SearchResult result, List<TrainCapacity> lst, SearchWrapper s, String direction, boolean isReturnJourney) {
		// TODO Auto-generated method stub
		int from = s.departingStation1 - 'A';
		int to = s.arrivalStation1 - 'A';
		TrainCapacity tc;
		int countFirst = 0;
		int countSecond = 0;
		//System.out.println("From " + from + " to " + to);
		for(int i = 0; i<lst.size(); i++){
			tc = lst.get(i);
			// If this is express train then make sure it stops both at source and destination to consider it for travel
			if(tc.getTrainNumber().endsWith("00")){
				if(!canExpressBeTaken(s.departingStation1, s.arrivalStation1)){continue;}
			}
			int lastDroppingStation = 0;
			if(!isReturnJourney && countFirst < 5){
				if(direction.equals("SB")){lastDroppingStation = getLastDroppingStationSB(tc, from, to, s);}
				else if(direction.equals("NB")){lastDroppingStation = getLastDroppingStationNB(tc, from, to, s);}
				if(lastDroppingStation == to){
				//	System.out.println("So can travel with this train");
					SearchResponse response = createResponseObject(tc.trainNumber, s.departingStation1, s.arrivalStation1, s.departingDate1, s);
					result.getSearchResponse().add(response);
					countFirst++;
					
				}
			}else if(isReturnJourney && countSecond < 5){
				if(direction.equals("SB")){lastDroppingStation = getLastDroppingStationNB(tc, to, from, s);}
				else if(direction.equals("NB")){lastDroppingStation = getLastDroppingStationSB(tc, to, from, s);}
				if(lastDroppingStation == from){
			//		System.out.println("So can travel with this train");
					SearchResponse response = createResponseObject(tc.trainNumber, s.arrivalStation1, s.departingStation1, s.departingDate2, s);
					//SearchResponse response = createResponseObject(tc.trainNumber, s.departingStation1, s.arrivalStation1, s.departingDate1, s);
					result.getReturnResponse().add(response);
					//result.getSearchResponse().add(response);
					countSecond++;
					
				}
			}
			if(countFirst>=5 && countSecond>=5){break;}
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

//	private SearchResponse createResponseObject(String trainNumber, Character departingStation, Character arrivalStation, int passengerCount, String departingDate, String connections, String ticketType) {
		// TODO Auto-generated method stub
		private SearchResponse createResponseObject(String trainNumber, Character departingStation, Character arrivalStation, String departingDate, SearchWrapper s) {
			SearchResponse response = new SearchResponse();
			response.setTrainNumber(trainNumber);
			response.setDepartingStation(departingStation);
			response.setArrivalStation(arrivalStation);
			response.setDepartingTime(getTime(trainNumber, departingStation, "D"));
			response.setArrivalTime(getTime(trainNumber, arrivalStation, "A"));
			response.setFare("$" + (getFare(trainNumber, departingStation, arrivalStation) + 1));
			response.setPassengerCount(s.passengerCount);
			response.setDepartingDate1(departingDate);
			response.setConnections(s.connections);
			response.setTicketType(s.ticketType);
			return response;
	}
	
	private int getFare(String trainNumber, Character departingStation, Character arrivalStation) {
		int start = departingStation - 'A';
		int t = arrivalStation - 'A';
		int cost = 0;
		if(start < t){
			cost = (t - start)/5;
			if((t-start)%5 != 0){cost++;}
		}
		else{
			cost =(start-t)/5;
			if((start-t)%5 != 0){cost++;}
		}
	//	System.out.println("Cost calculated is **************** " + cost + " plus $1 for transaction on whole");
		return trainNumber.endsWith("00")?cost*2:cost;
	}

	private String getTime(String trainNumber, Character departingStation, String bound) {
		// TODO Auto-generated method stub
		int f = departingStation - 'A';
		if(trainNumber.endsWith("0600") && f == 0){return "06:00";} // if its the first station, no need to calculate any duration
		String type = trainNumber.endsWith("00")?"Express":"Regular";
		String departingTime = "";
		int timeTravelled = 0;
		if(trainNumber.startsWith("SB")){
			if(type.equals("Express")){
				int start = 1;
				while(start++ <= f){
					timeTravelled +=5; // duration to travel to next station
					if(start%5 == 0){timeTravelled +=3;}  // duration of stopping at that station
				}
			}else{
				timeTravelled = (f - 0)*8;
			}
		}else if(trainNumber.startsWith("NB")){
			if(type.equals("Express")){
				int start = 25;
				while(start-- >= f){
					timeTravelled +=5; // duration to travel to next station
					if(start%5 == 0){timeTravelled +=3;}  // duration of stopping at that station
				}
			}else{
				timeTravelled = (25 - f)*8;
			}
		}
		if(bound.equals("A")){timeTravelled -=3;}
		int startingTimeInMinutes = Integer.parseInt(trainNumber.substring(2));
		int departingTimeinMinutes = ((startingTimeInMinutes/100)*60 + (startingTimeInMinutes%100) + timeTravelled);
		String hours = (departingTimeinMinutes/60<10)?("0" + departingTimeinMinutes/60):(String.valueOf(departingTimeinMinutes/60));
		String minutes =  (departingTimeinMinutes%60<10)?("0" + departingTimeinMinutes%60):(String.valueOf(departingTimeinMinutes%60));
		return (hours + ":" + minutes);
	}
	
	private int getLastDroppingStationSB(TrainCapacity tc, int from, int to, SearchWrapper s) {
		// TODO Auto-generated method stub
		int i = from;
		for(; i<=to; i++){
			char currentStation = (char)('A' + i);
			String str = currentStation + "_capacity";
			try {
				Field field = tc.getClass().getDeclaredField(str);
				int currentCapacity = ((Integer) field.get(tc)).intValue();
				if(currentCapacity < s.passengerCount){
					if(i == from){
						return -1;
					}else{
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
	//	System.out.println("Can drop at destination " + (i-1));
		//since i has increaed by above i++
		return i-1;
	}
//END: P-A
	
	
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
	@RequestMapping(value = "/trains/cancelTrain", method = RequestMethod.POST)
    public ResponseEntity<?> updateTrainStatus(@RequestBody String payload) {
		try{
			JSONObject jsonObj = new JSONObject(payload.toString());
			String trainNumber=jsonObj.has("trainNumber")==true ? jsonObj.getString("trainNumber"):"";
			String date =jsonObj.has("date")==true ? jsonObj.getString("date").split("T")[0] : "";
			if (trainNumber.equals("") || date.equals("")) {
	            return new ResponseEntity<>(new Response(404,"Invalid Arguements"), HttpStatus.NOT_FOUND);
	        } 
			//Change status field in respective train capacity repository
			List<TrainCapacity> trainCapacityLst=trainCapacityRepository.getTrainForStatusChange(trainNumber,date);
			if(trainCapacityLst!=null && trainCapacityLst.size()>0){
				TrainCapacity t=trainCapacityLst.get(0);
				t.setStatus("Cancelled");
				TrainCapacity updatedTrainCapacity=trainCapacityRepository.save(t);
				
				//Passenger re-booking
				TrainCapacityHelper obj= new TrainCapacityHelper();
				obj.cancelTrain(trainNumber,date,updatedTrainCapacity,ticketRepository);
		//		System.out.println("****in updateTrainStatus ");
				return new ResponseEntity<>(updatedTrainCapacity,HttpStatus.OK);
			}
			
			return new ResponseEntity<>(null,HttpStatus.OK);
		}
		catch(Exception e){
	//		System.out.println("****in exeption "+e.getMessage());
			return new ResponseEntity<>("BadRequest",HttpStatus.NOT_FOUND);
		}
	}
	
}
