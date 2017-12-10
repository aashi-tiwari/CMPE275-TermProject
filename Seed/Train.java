[12:50 AM, 12/10/2017] Pranjali Usa: import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Train {

	public static void main(String[] args) throws ParseException {
		LocalDate localDate = LocalDate.now();
		String da = String.valueOf(localDate);
		List<String> query = new ArrayList<String>();
		for(int i = 0; i< 61; i++){
			String s = "insert into Train (train_Number,type,direction,capacity,start_time,status) values (";
			String number = "0600";
			while(!number.equals("2115")){
				String type = number.endsWith("00")?"Express":"Regular";
				String q = s +"\"" +  "SB"+ number +"\"" +", \"" + type +"\"" + ", \"SB\", 5, "+number + ", \"Scheduled\"" + ");";
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

}
