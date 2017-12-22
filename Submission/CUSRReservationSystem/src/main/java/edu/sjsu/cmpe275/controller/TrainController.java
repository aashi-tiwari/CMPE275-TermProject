package edu.sjsu.cmpe275.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.util.*;

import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.validation.Valid;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
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
import edu.sjsu.cmpe275.model.Ticket;
import edu.sjsu.cmpe275.model.TicketResponse;
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
			System.out.println("************in getTrain "+payload);
			JSONObject jsonObj = new JSONObject(payload.toString());
			//Create Search Wrapper for all search parameters requested
			SearchWrapper s= new SearchWrapper();
			s.exactTime1 = 0;
			String isRoundTripStr=jsonObj.has("isRoundTrip")==true ? jsonObj.getString("isRoundTrip"):"false";
			s.isRoundTrip=Boolean.valueOf(isRoundTripStr);
			s.userId=jsonObj.has("userId")==true ? jsonObj.getString("userId"):"";
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
			s.departingDate2 = jsonObj.has("departingDate2")==true ? jsonObj.getString("departingDate2").split("T")[0]:"";
			System.out.println("*****s.departingDate2= "+s.departingDate2);
			//AASHI/PRANJALI
			s.passengerCount = jsonObj.has("passengerCount")==true ?jsonObj.getInt("passengerCount"):1;
			s.connections = jsonObj.has("connections")==true ? jsonObj.getString("connections"):"";
			//AASHI/PRANJALI
		    System.out.println("PASSENGERS " + s.passengerCount);
			//Check SB or NB 
			if(s.departingStation1.equals(s.arrivalStation1)){
				return new ResponseEntity<>(new Response(400, "Stations cannot be same"), HttpStatus.BAD_REQUEST);
			}
			
			SearchResult result=searchTrains(s);
	        return new ResponseEntity<>(result, HttpStatus.OK);
	       //END: AASHI/PRNAJALI
    	}
    	catch(Exception e){
    		System.out.println("exception="+e);
    		return new ResponseEntity<>("BadRequest",HttpStatus.BAD_REQUEST);
    	}
    }
	
	public SearchResult searchTrains(SearchWrapper s){
		try{
			String direction="",returnDirection="";
			if(s.departingStation1!=null && s.arrivalStation1!=null){
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
				System.out.println("************in searchTrains direction="+direction);
				//Query to get TrainCapacity records
				TrainCapacityHelper helper= new TrainCapacityHelper();
				Map<String,List<TrainCapacity>> mapStringToLstCapacity=helper.queryTrainCapacityRecords(s,direction,returnDirection,trainCapacityRepository);
				onewayLst=mapStringToLstCapacity.get("oneway");
				returnLst=mapStringToLstCapacity.get("return");
				System.out.println("************in searchTrains onewayLst.size= "+onewayLst.size());
				System.out.println("************in searchTrains returnLst.size= "+returnLst.size());
				//START: AASHI/PRANJALI
				SearchResult result = getTrains(onewayLst,returnLst, s,direction);
				return result;
			}
			else{
				System.out.println(" Invalid values of stations");
				return null;
			}
			
		}
		catch(Exception e){
    		System.out.println("in searchTrains exception="+e);
    		return null;
    	}
	}
	
//START: PRANJALI/AASHI
	//helper method to select top 5 trains
	public SearchResult getTrains(List<TrainCapacity> lst, List<TrainCapacity> returnLst, SearchWrapper s, String direction) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JSONException {
		// TODO Auto-generated method stub
		SearchResult result = new SearchResult();
		SearchResult upJourney = new SearchResult();
		SearchResult downJourney = new SearchResult();
		System.out.println("s.connections"+s.connections);
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
//			if(s.exactTime1 != 0){
//				System.out.println("EXACT TIME IS       " + s.exactTime1);
//				int currentTime = Integer.parseInt(tc1.getTrainNumber().substring(2));
//				if(currentTime<s.exactTime1){continue;}
//			}
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
							response1.setFare(totalFare);
							response2.setFare(totalFare);
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
							response1.setFare(totalFare);
							response2.setFare(totalFare);
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
		//System.out.println("in getNoneConnectionTrains");
		//System.out.println("From " + from + " to " + to);
		for(int i = 0; i<lst.size(); i++){
			tc = lst.get(i);
//			if(s.exactTime1 != 0){
//				System.out.println("EXACT TIME IS       " + s.exactTime1);
//				int currentTime = Integer.parseInt(tc.getTrainNumber().substring(2));
//				if(currentTime<s.exactTime1){continue;}
//			}
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
		response.setDepartingDate2(s.departingDate2);
		response.setConnections(s.connections);
		response.setTicketType(s.ticketType);
		response.setUserId(s.userId);
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

	//Getting departing or arrival time
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
	
	// Put request to cancel train
	@CrossOrigin
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
			System.out.println("****in updatedTrainCapacity trainCapacityLst="+trainCapacityLst.size());
			if(trainCapacityLst!=null && trainCapacityLst.size()>0){
				TrainCapacity t=trainCapacityLst.get(0);
				ResponseEntity<?> ret=doTimeValidationForTrain(t);
				if(ret!=null)
					return ret;
				t.setStatus("Cancelled");
				TrainCapacity updatedTrainCapacity=trainCapacityRepository.save(t);
				//Passenger re-booking
				return this.cancelTrain(trainNumber,date,updatedTrainCapacity);
				
				//return new ResponseEntity<>(updatedTrainCapacity,HttpStatus.OK);
			}
			
			return new ResponseEntity<>(null,HttpStatus.OK);
		}
		catch(Exception e){
			System.out.println("****in exeption "+e.getMessage());
			return new ResponseEntity<>("BadRequest",HttpStatus.NOT_FOUND);
		}
	}
	
	public ResponseEntity<?> cancelTrain(String trainNumber,String date,TrainCapacity updatedTrainCapacity)throws IOException, ParseException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		try {
			//Get all tickets for that train on given date
			List<Ticket> lstTickets=ticketRepository.searchTicketsByTrainAndDate(trainNumber,date);
			System.out.println("****in cancelTrain lstTickets"+lstTickets.size());
			//Create a set of all the transaction Id's of queried tickets
			Map<String,Set<Ticket>> mapTransactionIdToSetTickets= new HashMap<String,Set<Ticket>>();
			for(Ticket t: lstTickets){
				if(!t.getTransactionId().isEmpty()){
					Set<Ticket> lst= new HashSet<Ticket>();
					if(mapTransactionIdToSetTickets.containsKey(t.getTransactionId()))
						lst=mapTransactionIdToSetTickets.get(t.getTransactionId());
					lst.add(t);
					mapTransactionIdToSetTickets.put(t.getTransactionId(), lst);		
				}
			}
			//Query all tickets and populate mapTransactionIdToSetTickets only for tickets with transaction ids in map
			List<Ticket> lstAllTickets=ticketRepository.searchAllTickets();
			//Iterate on all tickets to populate mapTransactionIdToLstTickets, only for tickets in transactionIds
			for(Ticket t: lstAllTickets){
				//Consider ticket only if its transaction id is in transactionIds
				if(mapTransactionIdToSetTickets.containsKey(t.getTransactionId())){
					Set<Ticket> setTickets=mapTransactionIdToSetTickets.get(t.getTransactionId());
					setTickets.add(t);
					mapTransactionIdToSetTickets.put(t.getTransactionId(), setTickets);	
				}
			}
			//System.out.println("****in cancelTrain mapTransactionIdToSetTickets "+mapTransactionIdToSetTickets.size());
			
			//For each transactionId get original search criteria and call Search trains
			//Delete old ticket records
			Set<Ticket> setTicketsToDelete= new HashSet<Ticket>();
			for(String transactionId:mapTransactionIdToSetTickets.keySet()){
				String bookingUser="";
				//Get all tickets for this transaction
				Set<Ticket> setTickets= mapTransactionIdToSetTickets.get(transactionId);
				//System.out.println("****in cancelTrain setTickets "+setTickets.size());
				SearchWrapper s= new SearchWrapper();
				for(Ticket t: setTickets){
					if(t.getEmail()!=null && !t.getEmail().isEmpty())
						bookingUser=t.getEmail();
					if(t.getJourneyType()!=null && !t.getJourneyType().isEmpty()){
						if(t.getJourneyType().equalsIgnoreCase("OW") || t.getJourneyType().equalsIgnoreCase("OWFH")){
							s.departingStation1=t.getDepartingStation();
							if(s.arrivalStation1==null)
								s.arrivalStation1=t.getArrivalStation();
							s.departingDate1=t.getTravelDate().toString();
							s.passengerCount=t.getPassengerCount();
							s.ticketType=t.getTicketType();
							s.connections=t.getConnections();
						}
						else if(t.getJourneyType().equalsIgnoreCase("RT")||t.getJourneyType().equalsIgnoreCase("RTFH")){
							s.departingStation2=t.getDepartingStation();
							if(s.arrivalStation2==null)
								s.arrivalStation2=t.getArrivalStation();
							s.departingDate2=t.getTravelDate().toString();
							s.isRoundTrip=true;
							s.passengerCount=t.getPassengerCount();
							s.ticketType=t.getTicketType();
							s.connections=t.getConnections();
						}//Get final arrival station from second half
						else if(t.getJourneyType().equalsIgnoreCase("OWSF")){
							s.arrivalStation1=t.getArrivalStation();
						}
						//Get final arrival station from second half
						else if(t.getJourneyType().equalsIgnoreCase("RTSF")){
							s.arrivalStation2=t.getArrivalStation();
						}
					}
					setTicketsToDelete.add(t);
				}
				//Search Trains for using the original search criteria of this transaction Id
				SearchResult result=this.searchTrains(s);
				ObjectMapper mapperObj = new ObjectMapper();
				
				//Book the ticket using 1st search result
				if(result!=null && result.getSearchResponse().size()>0){
					SearchResponse firstResult=result.getSearchResponse().get(0);
					String jsonFirstResultStr = mapperObj.writeValueAsString(firstResult);
					System.out.println("----in cancelTrain jsonFirstResultStr= "+jsonFirstResultStr.toString());
					//ResponseEntity<?> newTicket= createTicketRecord(jsonFirstResultStr,bookingUser);
				}
				else{ //No search results available, hence cannot do booking. SEND EMAIL
					String subject=trainNumber+" : Train cancelled";
					String message="We regret to inform you that the train#"+ trainNumber+" date for "+ date + " has been cancelled and your booking has been cancelled. Sorry for in-convenience";
					TrainCapacityHelper.sendEmail(bookingUser,subject,message);
				}
			}
			//Delete old ticket records
			if(setTicketsToDelete.size()>0){
				ticketRepository.delete(setTicketsToDelete);
				System.out.println("*****delete tickets successfull");
			}
		}
	 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	 	}
		return null;
	}
	
	/*
	 * START: BELOW METHODS ARE COPY FROM TICKET CONTROLLER
	 */
	/*
	 * Given request body, creates a ticket record in database and updates traincapacity_repository records for capacity changes
	 */
	public  ResponseEntity<?> createTicketRecord(String payload) throws IOException{
		TicketResponse response = new TicketResponse();
		//Booking confirmation email params
		String transId="";
		String travelDate="";
		String route="";
		String userId="";
		String subject="Train reservation";
		String msg="";
		try {
			JSONObject jsonObj = new JSONObject(payload.toString());
			JSONArray returnResponse = (JSONArray) jsonObj.get("returnResponse");
//			boolean isTwoWayJourney = true;
//			if(returnResponse.length()==0){
//				isTwoWayJourney = false;
//			}
//			if(!isTwoWayJourney){
//				response =  createTicketAndUpdateCapacityOneWay(payload, isTwoWayJourney);
//			}else{
//				response =  createTicketAndUpdateCapacityOneWay(payload, isTwoWayJourney);
//			}
			if(returnResponse.length()==0){
				System.out.println("INSIDE ONE SIDE JOURNEY CHOICE");
				response =  createTicketAndUpdateCapacityOneWay(payload, "OneWay");
				if(response.getSearchResponse().size() == 1){response.getSearchResponse().get(0).setJourneyType("OW");}
				else if(response.getSearchResponse().size() == 2){
					response.getSearchResponse().get(0).setJourneyType("OWFH");
					response.getSearchResponse().get(1).setJourneyType("OWSH");
				}
				List<Ticket> ticketsToUpdate = response.getSearchResponse();
				for(Ticket t : ticketsToUpdate){
					ticketRepository.save(t);
				}
				//return new ResponseEntity<>(response, HttpStatus.OK);
			}
			else{
				System.out.println("INSIDE TWO WAY JOURNEY CHOICE");
				TicketResponse result = new TicketResponse();
				result =  createTicketAndUpdateCapacityOneWay(payload, "OneWay");
				List<Ticket> tickets = result.getSearchResponse();
				for(Ticket t : tickets){
					response.getSearchResponse().add(t);
				}
				if(result.getSearchResponse().size() == 1){result.getSearchResponse().get(0).setJourneyType("OW");}
				else if(result.getSearchResponse().size() == 2){
					result.getSearchResponse().get(0).setJourneyType("OWFH");
					result.getSearchResponse().get(1).setJourneyType("OWSH");
				}
				String transactionId = result.getSearchResponse().get(0).getTransactionId();
				result =  createTicketAndUpdateCapacityOneWay(payload, "TwoWay");
				if(result.getSearchResponse().size() == 1){result.getSearchResponse().get(0).setJourneyType("RT");System.out.println("Setting return with no connection, size: " + result.getSearchResponse().size());}
				else if(result.getSearchResponse().size() == 2){
					result.getSearchResponse().get(0).setJourneyType("RTFH");
					result.getSearchResponse().get(1).setJourneyType("RTSH");
					System.out.println("Setting return with ONE connection, size: " + result.getSearchResponse().size());
				}
//				if(result.getReturnResponse().size() == 1){result.getReturnResponse().get(0).setJourneyType("RT");}
//				else if(result.getReturnResponse().size() == 2){
//					result.getReturnResponse().get(0).setJourneyType("RTFH");
//					result.getReturnResponse().get(1).setJourneyType("RTSH");
//				}
				List<Ticket> ticketsTwo = result.getSearchResponse();
				for(Ticket t : ticketsTwo){
					t.setTransactionId(transactionId);
					ticketRepository.save(t);
					response.getReturnResponse().add(t);
				}
				
			}
			//Send booking confirmation email
			if(response.getSearchResponse().size() >0){
				transId=response.getSearchResponse().get(0).getTransactionId();
				userId=response.getSearchResponse().get(0).getEmail();
				travelDate=response.getSearchResponse().get(0).getTravelDate().toString();
				route=response.getSearchResponse().get(0).getDepartingStation().toString()+" - ";
				if(response.getSearchResponse().size() ==2)
					route+=response.getSearchResponse().get(1).getArrivalStation().toString();
				else
					route+=response.getSearchResponse().get(0).getArrivalStation().toString();
			}
			subject+="("+transId+")" + " | "+travelDate+" | "+route+" | "+userId;
			msg="<h1>Ready to travel!</h1> <br/> Your train booking with booking id "+transId+" has been confirmed. Thank You!";
			TrainCapacityHelper.sendEmail(userId,subject,msg);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
		private TicketResponse createTicketAndUpdateCapacityOneWay(String payload, String way) {
			// TODO Auto-generated method stub
		System.out.println("Updating direct train with no connections");
		TicketResponse response = new TicketResponse();
		System.out.println("In try");
		try{
			JSONObject jsonObj1 = new JSONObject(payload.toString());
			//START: P-A
			JSONArray searchResponse;
			if(way.equals("OneWay")){
				System.out.println("This is one way");
				searchResponse = (JSONArray) jsonObj1.get("searchResponse");
			}else{
				System.out.println("This is two way");
				searchResponse = (JSONArray) jsonObj1.get("returnResponse");
			}
			JSONObject  jsonObj = (JSONObject) searchResponse.get(0);
			JSONArray connected = (JSONArray) jsonObj.get("connected");
			Ticket ticket = new Ticket();
			Ticket ticketObj = new Ticket();
			if(connected.isNull(0)){
				
				ticket = getTicketObject(jsonObj, way);
				updateCapacity(ticket);
				ticketObj = ticketRepository.save(ticket);
				response.getSearchResponse().add(ticketObj);
			}else{
				JSONObject first = (JSONObject) connected.get(0);
				JSONObject second = (JSONObject) connected.get(1);
				ticket = getTicketObject(first, way);
				ticket.setFare(Integer.parseInt(jsonObj.getString("fare").substring(1)));
				ticket.setEmail(jsonObj.getString("userId"));
				//START: Saving 1st ticket and updating the train_capacity
				updateCapacity(ticket);
				ticketObj = ticketRepository.save(ticket);
				response.getSearchResponse().add(ticketObj);
				String transactionId = ticket.getTransactionId();
				ticket = getTicketObject(second, way);
				ticket.setFare(Integer.parseInt(jsonObj.getString("fare").substring(1)));
				ticket.setTransactionId(transactionId);
				ticket.setEmail(jsonObj.getString("userId"));
				updateCapacity(ticket);
				ticketObj = ticketRepository.save(ticket);
				response.getSearchResponse().add(ticketObj);
			}
			//END: P-A
			//JsonObj.has("searchResponse")==true ? jsonObj.getBoolean("isRoundTrip"):false;
		}
		catch(Exception e){
			System.out.println("exception="+e);
		}
		return response;
	}
	
		//START: P
		public  void updateCapacity(Ticket ticket) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			// TODO Auto-generated method stub
			List<TrainCapacity> list = trainCapacityRepository.searchTrainsToUpdateCapacity(ticket.getTrainNumber(), ticket.getTravelDate());
			System.out.println("GETTING LIST***********************************************");
			TrainCapacity tc = list.get(0);
			int from = ticket.getDepartingStation() - 'A';
			int to = ticket.getArrivalStation() - 'A';
			if(from < to){
				for(int i = from; i<to; i++){
					char station = (char)('A' + i);
					String str = station + "_capacity";
					Field field = tc.getClass().getDeclaredField(str);
					int currentCapacity = field.getInt(tc);
					int newCapacity = currentCapacity - ticket.getPassengerCount();
					field.setInt(tc, newCapacity);
					System.out.println("After saving to database SB");
					//field.setInt(tc, 5);
				}
			}else if(to < from){
				for(int i = from; i>to; i--){
					char station = (char)('A' + i);
					String str = station + "_capacity";
					Field field = tc.getClass().getDeclaredField(str);
					int currentCapacity = field.getInt(tc);
					int newCapacity = currentCapacity - ticket.getPassengerCount();
					field.setInt(tc, newCapacity);
					//field.setInt(tc, 5);
					System.out.println("After saving to database NB");
				}
			}
			TrainCapacity tcObj = trainCapacityRepository.save(tc);
			System.out.println("After saving to database ");
		}

		public  Ticket getTicketObject(JSONObject jsonObj, String way) throws ParseException {
			// TODO Auto-generated method stub
			Ticket ticket = new Ticket();
			ticket.setTransactionId(String.valueOf(UUID.randomUUID()));
			try {
				ticket.setTrainNumber(jsonObj.getString("trainNumber"));
				if(jsonObj.has("userId")){
					ticket.setEmail(jsonObj.getString("userId"));
				}
				int passengerCount = Integer.parseInt(jsonObj.getString("passengerCount"));
				ticket.setPassengerCount(passengerCount);
				//ticket.setPassengerCount(jsonObj.getInt("passengerCount"));
				ticket.setConnections(jsonObj.getString("connections"));
				ticket.setTicketType(jsonObj.getString("ticketType"));
				ticket.setDepartingStation(jsonObj.getString("departingStation").charAt(0));
				ticket.setArrivalStation(jsonObj.getString("arrivalStation").charAt(0));
			   // Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(jsonObj.getString("departingDate1"));
				java.sql.Date sqlDate;
				if(way.equalsIgnoreCase("OneWay")){sqlDate = java.sql.Date.valueOf( jsonObj.getString("departingDate1") );}
				else{sqlDate = java.sql.Date.valueOf( jsonObj.getString("departingDate2") );}
			    ticket.setTravelDate(sqlDate);
			    ticket.setDepartureTime(jsonObj.getString("departingTime"));
			    ticket.setArrivalTime(jsonObj.getString("arrivalTime"));
			    ticket.setFare(Integer.parseInt(jsonObj.getString("fare").substring(1)));
			    //Start: snehal
			   // ticket.setJourneyType(jsonObj.has("journeyType")?jsonObj.getString("journeyType"):"");
			    //End : Snehal
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ticket;
		}
	
	/*
	 * END: ABOVE METHODS ARE COPY FROM TICKET CONTROLLER
	 */
	
	//Check if departure of train to be cancelled is > 3hrs from now
	private ResponseEntity<?> doTimeValidationForTrain(TrainCapacity tc){
		try{
			//Search start time of train in train capacity
			String trainNumber = tc.getTrainNumber();
			Train t=trainRepository.findOne(trainNumber);
	    	if (t == null) {
	    		return new ResponseEntity<>(new Response(404,"Invalid train number "), HttpStatus.NOT_FOUND);
	        } 
	    	
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        Date travelDate = sdf.parse(tc.getTravelDate().toString());
	        Date today = sdf.parse(sdf.format(new Date()).toString());
	        System.out.println("travelDate : " + sdf.format(travelDate));
	        System.out.println("today : " + sdf.format(today));
	        
	        if (travelDate.before(today)) {
	        	return new ResponseEntity<>(new Response(404,"Cannot cancel a train in past"), HttpStatus.BAD_REQUEST);
	        } else if (travelDate.after(today)) {
	            return null;
	        } else { //Today and travel date are same. Hence, compare time
	        	int trainStartTime=t.getStartTime();
	        	// System.out.println("****in doTimeValidationForTrain trainStartTime="+trainStartTime);
				SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
			    Date now = new Date();
			    int currentTime = Integer.parseInt(sdfTime.format(now).toString().replace(":", ""));
			  //  System.out.println("****in doTimeValidationForTrain currentTime="+currentTime);
			    if(trainStartTime-currentTime < 0){
			    	return new ResponseEntity<>(new Response(404,"Cannot cancel a train in past"), HttpStatus.BAD_REQUEST);
			    }
			    else if(trainStartTime-currentTime < 300){
			    	return new ResponseEntity<>(new Response(404,"Cannot cancel a train within 3 hours of its departure time"), HttpStatus.BAD_REQUEST);
			    }
			    //If valid case, return null
		    	return null;
	        }
	    	
    	}
    	catch(Exception e){
    		return new ResponseEntity<>("BadRequest",HttpStatus.BAD_REQUEST);
    	}
		
	}
	
	
}
