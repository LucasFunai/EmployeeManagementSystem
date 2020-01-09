package ems;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class TextLogFiler implements LogFiler{
	static {
		File file = new File(".\\EMSLogFiles\\testFile");
		file.getParentFile().mkdirs();
		try {
			FileHandler fileHandler = new FileHandler(".\\EMSLogFiles\\EMSErrorLog%g.txt");
			fileHandler.setFormatter(new SimpleFormatter());
			LogPrinter.addHandler(fileHandler);
			LogPrinter.setUseParentHandlers(false);

		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void saveStackTrace(Exception e) {
		LogPrinter.log(Level.SEVERE,"Exception thrown by " + e.getClass().getCanonicalName() + " Cause :" + e.getCause() + ":" + e.getMessage(), e);
		for(java.util.logging.Handler l : LogPrinter.getHandlers()) {
			l.close();
		}
	};

}