package CMPE275;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class Main {
	public static void main(String args[]) throws ParseException{
//		String s = "insert into train_capacity (train_number, travel_date) values (";
//		String number = "0600";
//		List<String> query = new ArrayList<String>();
//		while(!number.equals("2115")){
//			String q = s + "NB"+ number + ");";
//			query.add(q);
//			int j = Integer.parseInt(number) + 15;
//			number ="0" + String.valueOf(j);
//			if(number.substring(number.length()-2).equals("60")){
//				j = Integer.parseInt(number) + 40;
//				number = "0" + String.valueOf(j);
//			}
//			if(number.length() > 4){
//				number = number.substring(1);
//			}
//		}
//		for(String p : query){
//			System.out.println(p);
//		}
		LocalDate localDate = LocalDate.now();
		String da = String.valueOf(localDate);
		List<String> query = new ArrayList<String>();
		for(int i = 0; i< 30; i++){
			da = getNextDate(da);
			String s = "insert into train_capacity (train_number, travel_date) values (";
			String number = "0600";
			while(!number.equals("2115")){
				String q = s + "NB"+ number +"," + "DATE '" + da + "'"+ ");";
				query.add(q);
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
		for(String p : query){
			System.out.println(p);
		}
		
	}
	public static String getNextDate(String  curDate) throws ParseException {
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		  final Date date = format.parse(curDate);
		  final Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);
		  calendar.add(Calendar.DAY_OF_YEAR, 1);
		  return format.format(calendar.getTime());
} 
}
