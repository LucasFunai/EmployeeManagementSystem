package ems;

public interface Launcher {
	
	public EMSDataHandler returnHandler();
	
	public Login returnLogin();
	
	public Printer returnPrint();
	
	public Browser returnBrowser();
	
	public LogFiler returnLogFiler();

}
