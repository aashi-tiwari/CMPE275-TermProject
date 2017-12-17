// . STUB TO GET THE SEARCH RESULT BASED ON USER SEARCH

package CMPE275;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class Test {
	public static void main(String args[]) throws ParseException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		char[] stations = new char[26];
		List<trainCapacity> capacity = new ArrayList<trainCapacity>();
		LocalDate localDate = LocalDate.now();
		String da = String.valueOf(localDate);
		for(int i = 0; i< 2; i++){
			da = getNextDate(da);
			String s = "insert into train_capacity (train_number, travel_date) values (";
			String number = "0600";
			while(!number.equals("2115")){
				DateFormat parser =  new SimpleDateFormat("yyyy-MM-dd");
				capacity.add(new trainCapacity(1, "SB" + number, da, 5,5,5,5,5,5));
				int j = Integer.parseInt(number) + 15;
				number ="0" + String.valueOf(j);
				if(number.substring(number.length()-2).equals("60")){
					j = Integer.parseInt(number) + 40;
					number = "0" + String.valueOf(j);
				}
				if(number.length() > 4){
					number = number.substring(1);
				}
			}
		}
		//FEEDING END
		char from = 'C';
		char to = 'F';
		int f = from - 'A';
		int t = to - 'A';
		//System.out.println("f " + f + " t " + t );
		String date = "2017-12-18";
		int passengerCount = 1;
		
		String direction = (from-to)<0?"SB":"NB"; //SB
		List<trainCapacity> filtered = new ArrayList<trainCapacity>();
		List<trainCapacity> showing = new ArrayList<trainCapacity>();
		for(trainCapacity ti : capacity){
			if(ti.travelDate.equals(date)){
					filtered.add(ti);
			}
		}
		int count = 0;
		for(trainCapacity tc : filtered){
			boolean availability = true;
			//System.out.println("Looking for train " + tc.trainNumber);
			if(count < 5){
				for(int i = f; i<t ; i++){
					char c = (char)('A' + i);
					Field field = tc.getClass().getDeclaredField(String.valueOf(c));
					int seatsLeft = ((Integer) field.get(tc)).intValue();
				//	System.out.println("Station " + c + " has " + seatsLeft + " seats left");
					if(seatsLeft < passengerCount){
						availability = false;
					}
				}
				if(availability == true){
					showing.add(tc);
					count++;
				}
			}
			if(count>=5){break;}
		}
		//THIS IS FINAL RESULT TO RETURN TO USER
		for(trainCapacity tpa : showing){
			System.out.println(tpa.trainNumber);
		}
		//******************
		//******************
	}
       	public static String getNextDate(String  curDate) throws ParseException {
		  final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		  final Date date = format.parse(curDate);
		  final Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);
		  calendar.add(Calendar.DAY_OF_YEAR, 1);
		  String result = format.format(calendar.getTime());
		  return format.format(calendar.getTime());
} 
}
