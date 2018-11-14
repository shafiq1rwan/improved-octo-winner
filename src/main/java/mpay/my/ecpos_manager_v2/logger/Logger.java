package mpay.my.ecpos_manager_v2.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mpay.my.ecpos_manager_v2.property.Property;

public class Logger {

	final static String PROJECT_LOG_PATH = Property.getPROJECT_LOG_PATH();
	
	public static void writeActivity(String logActivity, String filename, String foldername) {
		Date toFDate = new Date();
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		String date = f.format(toFDate);
		FileWriter log;
		/* String logActivity = error; */

		try {

			String errorlogpath = PROJECT_LOG_PATH + "/" + foldername;
			File detectFile = new File(errorlogpath);
			detectFile.mkdirs();
			
			System.out.println("Can Write ? :" + detectFile.canWrite());

			log = new FileWriter(errorlogpath + "/" + date + filename + ".txt",
					true);
			BufferedWriter outbuff = new BufferedWriter(log);

			outbuff.write(((Calendar.getInstance()).getTime()).toString());
			outbuff.write(" : ");
			outbuff.write(logActivity);
			outbuff.newLine();
			outbuff.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeError (String logActivity, String filename, String foldername)
	{	
		Date toFDate = new Date();
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		String date = f.format(toFDate);
		FileWriter log;		
		
		try 
		{
	
			String errorlogpath = PROJECT_LOG_PATH+"/"+foldername;
			File detectFile = new File(errorlogpath);
			detectFile.mkdirs();
			
			System.out.println("Can Write ? :" + detectFile.canWrite());
						
			log = new FileWriter (errorlogpath +"/" + date + filename +".txt", true);
			BufferedWriter outbuff = new BufferedWriter (log);			
	
			outbuff.write( ((Calendar.getInstance()).getTime()).toString() );
			outbuff.write(" : ");
			outbuff.write(logActivity);
			outbuff.newLine();
			outbuff.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
