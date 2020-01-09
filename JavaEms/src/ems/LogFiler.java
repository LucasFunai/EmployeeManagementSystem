package ems;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface LogFiler{
	Logger LogPrinter = Logger.getAnonymousLogger();
	void saveStackTrace(Exception e);
	
}
