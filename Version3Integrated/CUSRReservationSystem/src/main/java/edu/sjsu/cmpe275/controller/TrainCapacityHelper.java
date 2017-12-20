package edu.sjsu.cmpe275.controller;

import java.io.IOException;
import java.util.ArrayList;
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
	
	public static void sendEmail(String userEmail,String subject,String msg) throws IOException {
		System.out.println("in sendEmail....");
		// Recipient's email ID needs to be mentioned.
	      String to = userEmail;
	      System.out.println("-----to="+to);
	      to="snehalphatangare@gmail.com";
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
}
