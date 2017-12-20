package edu.sjsu.cmpe275.controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.model.Ticket;
import edu.sjsu.cmpe275.model.TrainCapacity;
import edu.sjsu.cmpe275.repository.TicketRepository;
import edu.sjsu.cmpe275.repository.TrainCapacityRepository;

//@RestController
public class TrainCapacityHelper {
//	@Autowired
//	TrainCapacityRepository trainCapacityRepository;
	
	public TrainCapacityHelper(){
		
	}
	
	public void cancelTrain(String trainNumber,String date,TrainCapacity updatedTrainCapacity,TicketRepository ticketRepository){
		//Get all tickets for that train on given date
		List<Ticket> lstTickets=ticketRepository.searchTicketsByTrainAndDate(trainNumber,date);
		System.out.println("****in cancelTrain "+lstTickets.size());
		//Create a set of all the transaction Id's of queried tickets
		Map<String,Set<Ticket>> mapTransactionIdToLstTickets= new HashMap<String,Set<Ticket>>();
		Set<String> transactionIds= new HashSet<String>();
		for(Ticket t: lstTickets){
			if(!t.getTransactionId().isEmpty()){
				transactionIds.add(t.getTransactionId());
				Set<Ticket> lst= new HashSet<Ticket>();
				if(mapTransactionIdToLstTickets.containsKey(t.getTransactionId()))
					lst=mapTransactionIdToLstTickets.get(t.getTransactionId());
				lst.add(t);
				mapTransactionIdToLstTickets.put(t.getTransactionId(), lst);		
			}
		}
		List<String> lstT=new ArrayList<String>();
		lstT.addAll(transactionIds);
		System.out.println("****in cancelTrain transactionIds"+transactionIds.size());
		//Get the list of all ticket records grouped by transaction Id. All these tickets need to be cancelled
		//ticketRepository.searchTicketsByTransactionId();
//		List<Ticket> lstAllTickets=ticketRepository.searchTicketsByTransactionId(lstT);
//		System.out.println("****in cancelTrain tickets for all transaction ids "+lstAllTickets.size());
		
		//Iterate on all tickets to populate mapTransactionIdToLstTickets, only for tickets in transactionIds
//		for(Ticket t: lstAllTickets){
//			//Consider ticket only if its transaction id is in transactionIds
//			if(transactionIds.contains(t.getTransactionId())){
//				Set<Ticket> lst=mapTransactionIdToLstTickets.get(t.getTransactionId());
//				lst.add(t);
//				mapTransactionIdToLstTickets.put(t.getTransactionId(), lst);	
//			}
//		}
		
		//For each transactionId get original search criteria and call Search tickets
		//Form SearchWrapper
	}
	
	public Map<String,List<TrainCapacity>> queryTrainCapacityRecords(SearchWrapper s,String direction,String returnDirection,TrainCapacityRepository trainCapacityRepository){
		List<TrainCapacity> onewayLst = new ArrayList<TrainCapacity>();
		List<TrainCapacity> returnLst = new ArrayList<TrainCapacity>();
		Map<String,List<TrainCapacity>> mapStringToLstCapacity=new HashMap<String,List<TrainCapacity>>();
		System.out.println("s.ticketTyp= "+s.ticketType);
		//If Train type "REgular" specified
		if(s.ticketType.equalsIgnoreCase("Regular")){
			System.out.println("trainCapacityRepository= "+trainCapacityRepository);
			onewayLst=trainCapacityRepository.searchTrainsWithType(direction,s.departingDate1,"Regular");
			System.out.println("isRoundTrip= "+s.isRoundTrip);
			if(s.isRoundTrip)
				returnLst=trainCapacityRepository.searchTrainsWithType(returnDirection,s.departingDate2,"Regular");
		}
		else{
			onewayLst=trainCapacityRepository.searchTrains(direction,s.departingDate1);
			if(s.isRoundTrip)
				returnLst=trainCapacityRepository.searchTrains(returnDirection,s.departingDate2);
		}
		mapStringToLstCapacity.put("oneway", onewayLst);
		mapStringToLstCapacity.put("return", returnLst);
		return mapStringToLstCapacity;
	}
}
