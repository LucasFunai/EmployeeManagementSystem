package ems;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

//Last one to boot
public interface Browser{
	void initiate();
	void getDependencies(Launcher launch);
	//Use login's "UserIsValid". if true, its currentId changes to the corresponding id.
	//password is a char array for security reasons.
	boolean login(int id,char[] passWord);
	//Asking for ID, password, and evaluating is all here.
	void loginProceedure();
	//On text-based:Switch statements. On javafx: show the window.
	void waitCommands();
	void showEmpTable();
	//For evaluating whether the employee has cheated or not.
	int diffFromStart(Date date);
	String readPassword();
	int selectedId();
	String printAndGetInput(String s);
	void print(String s);
	void selectEmployee(int id);
	//both get and set for score must be protected against low ranks (less than 2)
	//get use current Id
	void getScore();
	void setScore(int score);
	//Accessible only from higher ranks (more than 2)
	void addEmployee(String passWord,String name,int rank);
	//Accessible from all ranks, but can only modify own
	void startAttendanceTimer();
	int returnTimerMinutes();
	void getAttendance(String yearAndMonth);
	//ALWAYS USE ID FROM LOGIN
	int returnBrowserId();	
	void saveAndQuit() throws IOException,SQLException;
	boolean timerStarted();
	//Normal Format is : yyyy:MM:dd but anything else works too
	LocalDateTime currentDate();
	boolean isSetup();
	//Must return true if there is no other employees.
	//Used for first time setups.
	void setRank(int rank);
	void requestQuit();
	void waitInput();
	boolean quitRequested();
	//If this is true, it will try to quit in the next loop end.
	//Can also be used to notify the system to halt a frozen method if you choose a multithread implementation
	
}
