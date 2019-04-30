package mpay.ecpos_manager.general.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Logger {

	public static String logPath = "ecpos-log";

	public static void writeActivity(String logActivity, String foldername) {
		Date toFDate = new Date();
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		String date = f.format(toFDate);
		FileWriter log;
		
		try {
			String activitylogpath = logPath + "/" + foldername;
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

	public static void writeError(Exception exception, String logActivity, String foldername) {
		Date toFDate = new Date();
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		String date = f.format(toFDate);
		FileWriter log;

		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string

			String errorlogpath = logPath + "/" + foldername;
			File detectFile = new File(errorlogpath);
			detectFile.mkdirs();

			log = new FileWriter(errorlogpath + "/" + date + "err.txt", true);

			BufferedWriter outbuff = new BufferedWriter(log);
			outbuff.write(((Calendar.getInstance()).getTime()).toString());
			outbuff.write(" : ");
			outbuff.write(logActivity);
			outbuff.write(sStackTrace);
			outbuff.newLine();
			outbuff.close();
		} catch (Exception e) {
		}
	}
}
