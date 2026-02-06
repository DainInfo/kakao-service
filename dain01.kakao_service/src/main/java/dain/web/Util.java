package dain.web;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Util {

	public static String Date(){
	
		LocalDate nowDate = LocalDate.now();	
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");        
	    String inputDate = nowDate.format(formatter);
		
	    SimpleDateFormat SDFin = new SimpleDateFormat("yyyyMMdd");
	    SimpleDateFormat SDFout = new SimpleDateFormat("yyyy-MM-dd");
	    Date chgInputDate = null;
		try {
			chgInputDate = SDFin.parse(inputDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(chgInputDate);
		calendar.add(Calendar.DATE, 1);
		String date = SDFout.format(calendar.getTime());
	    
		LocalTime nowTime = LocalTime.now();	
	    formatter = DateTimeFormatter.ofPattern("HH:mm:ss");        
	    String time = nowTime.format(formatter);
	    
	    return date + "T" + time;
	}
}
