package ems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Date;
import java.util.Properties;
import javax.sql.DataSource;



//This file is here for backup purposes. Setter was merged with Getter to make EMSDataHandler.

class SqlSetter implements Setter{
	Connection connection;
	Statement stmt;
	Browser browse;
	SqlDataHandler getter;
	LogFiler logFiler;


	public void initiate() {
		connection = getter.returnConnection();
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			browse.printAndGetInput("An exception occured. Please check the logFile for more info. Press Enter to exit.");
			logFiler.saveStackTrace(e);
			System.exit(-3);
		}
	}

	@Override
	public void saveAttendance() {
		if(browse.timerStarted()) {
			try {
				PreparedStatement prepared = connection.prepareStatement("INSERT into attendeddates (ID,Date,Minutes) VALUES (?,?,?)");
				prepared.setInt(1,browse.returnBrowserId());
				prepared.setDate(2,new Date(System.currentTimeMillis()));
				prepared.setInt(3,browse.returnTimerMinutes());
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				stmt = connection.createStatement();
				ResultSet result = stmt.executeQuery("SELECT NOW()");
				result.next();
				String serverTime = result.getString(1);
				java.util.Date serverDate = converter.parse(serverTime);
				boolean cheat = browse.diffFromStart(serverDate) < browse.returnTimerMinutes();
				if(cheat) {
					browse.printAndGetInput("Error in retrieving timer. Please call your manager.");
					browse.printAndGetInput("Got : " + Integer.toString(browse.returnTimerMinutes()) + "expected : " + browse.diffFromStart(serverDate));
					System.exit(-3);
				}
				prepared.executeUpdate();



			} catch (SQLException e) {
				browse.print("An exception occured. Please check the logFile for more info.");
				logFiler.saveStackTrace(e);
			} catch (ParseException e) {
				browse.print("An exception occured. Please check the logFile for more info.");
				logFiler.saveStackTrace(e);

			}
		}
	}


	@Override
	public boolean setPerformance(int id, int score) {
		String query = "UPDATE employeedata set Score = " + Integer.toString(score) + " WHERE ID = " + Integer.toString(id);
		try {
			stmt.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			browse.print("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
			return false;
		}

	}

	@Override
	public int setNewEmployee(String passWord, String name,int rank) {
		String query = "INSERT INTO employeedata(name,passwrd,`rank`,score) VALUES (";
		String values = "'" + name +  "'" + "," + "'" + passWord + "'" + "," + Integer.toString(rank) +
				"," + "1)";
		try {
			stmt.executeUpdate(query + values);
			ResultSet rs = stmt.executeQuery("Select ID from employeedata where name ='" + name +"'");
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			browse.print("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
			return -1;
		}

	}

	@Override
	public boolean setRank(int id, int rank) {
		String query = "UPDATE employeedata set Rank = " + Integer.toString(rank) + "WHERE ID = " + Integer.toString(id);
		try {
			stmt.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			browse.print("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
			return false;
		}
	}

	@Override
	public void getDependencies(Launcher launch) {
		getter = (SqlDataHandler) launch.returnHandler();
		browse = launch.returnBrowser();
		
	}

}
