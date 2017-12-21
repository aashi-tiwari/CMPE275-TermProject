package edu.sjsu.cmpe275.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.sjsu.cmpe275.model.Response;
import edu.sjsu.cmpe275.model.Train;
import edu.sjsu.cmpe275.model.TrainCapacity;
import edu.sjsu.cmpe275.repository.TrainCapacityRepository;

public class TrainCapacityHelper {
	public TrainCapacityHelper(){
		
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
	
//	private List<TrainCapacity> trainDepartureTimeValidations(SearchWrapper wrapper,List<TrainCapacity> lst){
//		try{
//			//Search start time of train in train capacity
//			String trainNumber = wrapper.
//			Train t=trainRepository.findOne(trainNumber);
//	    	if (t == null) {
//	    		return new ResponseEntity<>(new Response(404,"Invalid train number "), HttpStatus.NOT_FOUND);
//	        } 
//	    	
//	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//	        Date travelDate = sdf.parse(tc.getTravelDate().toString());
//	        Date today = sdf.parse(sdf.format(new Date()).toString());
//	        System.out.println("travelDate : " + sdf.format(travelDate));
//	        System.out.println("today : " + sdf.format(today));
//	        
//	        if (travelDate.before(today)) {
//	        	return new ResponseEntity<>(new Response(404,"Cannot cancel a train in past"), HttpStatus.BAD_REQUEST);
//	        } else if (travelDate.after(today)) {
//	            return null;
//	        } else { //Today and travel date are same. Hence, compare time
//	        	int trainStartTime=t.getStartTime();
//	        	// System.out.println("****in doTimeValidationForTrain trainStartTime="+trainStartTime);
//				SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
//			    Date now = new Date();
//			    int currentTime = Integer.parseInt(sdfTime.format(now).toString().replace(":", ""));
//			  //  System.out.println("****in doTimeValidationForTrain currentTime="+currentTime);
//			    if(trainStartTime-currentTime < 0){
//			    	return new ResponseEntity<>(new Response(404,"Cannot cancel a train in past"), HttpStatus.BAD_REQUEST);
//			    }
//			    else if(trainStartTime-currentTime < 300){
//			    	return new ResponseEntity<>(new Response(404,"Cannot cancel a train within 3 hours of its departure time"), HttpStatus.BAD_REQUEST);
//			    }
//			    //If valid case, return null
//		    	return null;
//	        }
//	    	
//    	}
//    	catch(Exception e){
//    		return new ResponseEntity<>("BadRequest",HttpStatus.BAD_REQUEST);
//    	}
//	}
	
	public static void sendEmail(String userEmail,String subject,String msg) throws IOException {
		System.out.println("in sendEmail....");
		// Recipient's email ID needs to be mentioned.
	      String to = userEmail;
	      if(to!=null){
		      System.out.println("-----to="+to);
		      //to="snehalphatangare@gmail.com";
		      // Sender's email ID needs to be mentioned
		      String from = "cmpe275sjsuteam14@gmail.com";
	
		      // Assuming you are sending email from localhost
		      String host = "localhost";
	
		      // Get system properties
		      Properties properties = System.getProperties();
	
		      // Setup mail server
		      properties.put("mail.smtp.starttls.enable", "true");
		      properties.put("mail.smtp.auth", "true");
		      properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		      properties.put("mail.smtp.port", "587");
	
		      // Get the default Session object.
		     // Session session = Session.getDefaultInstance(properties);
		      Session session = Session.getInstance(properties,
		              new javax.mail.Authenticator() {
		                protected PasswordAuthentication getPasswordAuthentication() {
		                    return new PasswordAuthentication(from, "phatangare");
		                }
		              });
	
		      try {
		         // Create a default MimeMessage object.
		         MimeMessage message = new MimeMessage(session);
	
		         // Set From: header field of the header.
		         message.setFrom(new InternetAddress(from));
	
		         // Set To: header field of the header.
		         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		         // Set Subject: header field
		         message.setSubject(subject);
	
		         // Send the actual HTML message, as big as you like
		         message.setContent(msg, "text/html");
	
		         // Send message
		         Transport.send(message);
		         System.out.println("******Sent message successfully....");
		      } catch (MessagingException mex) {
		    	  System.out.println("send mail exception...."+mex);
		         mex.printStackTrace();
		      }
	      }
	      else
	    	  System.out.println("-----in sendEmail to is blank=");
    }
}
