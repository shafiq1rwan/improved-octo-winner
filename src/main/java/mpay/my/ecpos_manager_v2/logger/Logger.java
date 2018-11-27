package mpay.my.ecpos_manager_v2.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mpay.my.ecpos_manager_v2.property.Property;

public class Logger {

	final static String PROJECT_LOG_PATH = Property.getPROJECT_LOG_PATH();
	
	public static void writeActivity(String logActivity, String foldername) {
		Date toFDate = new Date();
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		String date = f.format(toFDate);
		FileWriter log;

		try {
			String activitylogpath = PROJECT_LOG_PATH + "/" + foldername;
			File detectFile = new File(activitylogpath);
			detectFile.mkdirs();

			log = new FileWriter(activitylogpath + "/" + date + "act.txt", true);
			
			BufferedWriter outbuff = new BufferedWriter(log);
			outbuff.write(((Calendar.getInstance()).getTime()).toString());
			outbuff.write(" : ");
			outbuff.write(logActivity);
			outbuff.newLine();
			outbuff.close();
		} catch (Exception e) {
		}
	}
	
	public static void writeError (Exception exception, String logActivity, String foldername) {	
		Date toFDate = new Date();
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		String date = f.format(toFDate);
		FileWriter log;		
		
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			
			String errorlogpath = PROJECT_LOG_PATH + "/" + foldername;
			File detectFile = new File(errorlogpath);
			detectFile.mkdirs();
						
			log = new FileWriter (errorlogpath + "/" + date +"err.txt", true);
			
			BufferedWriter outbuff = new BufferedWriter (log);
			outbuff.write( ((Calendar.getInstance()).getTime()).toString() );
			outbuff.write(" : ");
			outbuff.write(logActivity);
			outbuff.write(sStackTrace);
			outbuff.newLine();
			outbuff.close();
		} catch (Exception e) {
		}
	}
}
