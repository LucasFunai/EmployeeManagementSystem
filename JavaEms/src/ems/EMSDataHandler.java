package ems;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

//First one to boot
public interface EMSDataHandler{
	void initiate();
	void getDependencies(Launcher launch);
	//ID RANK VERIFICATION MUST BE DONE IN GETTER AND NOT IN ANY OTHER CLASS
	int getRank(int id) throws IOException,SQLException;
	//getPerformance must be protected against low ranks (less than 2)
	ArrayList<Integer> getPerformance(int userId,int id) throws IOException,SQLException;
	ArrayList<String> returnAttendance(int userId,int id,int year,int month) throws IOException,SQLException;
	//Format : yyyy-mm
	String getName(int id) throws IOException,SQLException;
	boolean save() throws IOException,SQLException;
	boolean load() throws IOException,SQLException;
	boolean isConfigured();
	//Check if the handler knows where to get data from
	
	ArrayList<String> returnNames();
	ArrayList<String> returnIDs();
	//Check if the setter has a higher rank than the person being rated.
	boolean setPerformance(int id,int score);
	//Only for HIGH ranks. Return the auto increment id. (more than 3)
	int setNewEmployee(String passWord,String name,int rank);
	//Only for ADMIN (rank 5)
	boolean setRank(int id,int rank);
	void saveAttendance(int timerMinutes,boolean timerStarted);
	boolean setupDB(String address,String userName,String passWord);
	
}
