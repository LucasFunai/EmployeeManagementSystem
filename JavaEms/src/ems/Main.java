package ems;

import ems.FXEms.FXBLauncher;
import javafx.application.Application;

public class Main {
	

	public static void main(String args[]) {
		EMSLauncher launcher = new EMSLauncher(args); 
		launcher.start();
	}
	
	
}

class EMSLauncher implements Launcher{
	
	private Login logger;
	private EMSDataHandler dataHandler;
	private Printer printer;
	private Browser browser;
	private LogFiler logFiler;
	private String[] args;
	
	EMSLauncher(String arguments[]){
		args = arguments;
	}
	
	public void start() {
		if(!(args.length == 0)&& args[1].equals("--text")) {
			browser = new textBasedBrowser();
			logFiler = new TextLogFiler();
			if(!(args.length == 0) && args[0].equals("--local")) {
				dataHandler = new LocalDataHandler();
				logger = new LocalLogin();
				printer = new LocalPrinter();
				
				
				
			} else if(!(args.length == 0) && args[0].equals("--sql")) {

				dataHandler = new SqlDataHandler();
				logger = new SqlLogin();
				printer = new LocalPrinter();
			}



		} else if(args.length != 0 && args[1].equals("--gui")){
			if(!(args.length == 0) && args[0].equals("--local")) {
				Application.launch(FXBLauncher.class,"--local");
				
				
				
			} else if(!(args.length == 0) && args[0].equals("--sql")) {		
				Application.launch(FXBLauncher.class,"--sql");

				
				
				
				
				
				
				

			}
			dataHandler.getDependencies(this);
			logger.getDependencies(this);
			browser.getDependencies(this);
			dataHandler.initiate();
			browser.initiate();
		}
	}
	
	public EMSDataHandler returnHandler() {
		return dataHandler;
	}
	
	
	public Login returnLogin() {
		return logger;
	}
	public Printer returnPrint() {
		return printer;
	}
	public Browser returnBrowser() {
		return browser;
	}
	
	public LogFiler returnLogFiler() {
		return logFiler;
	}
}
