package ems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

public class SqlDataHandler implements EMSDataHandler{
	Connection conn = null;
	Properties config = new Properties();
	LogFiler logFiler;
	Printer printer;
	Statement stmt;
	MysqlDataSource ds;
	Login credentials;
	boolean configured;
	boolean inSetup;


	public void initiate() {
		FileInputStream reader = null;
		ds = new com.mysql.cj.jdbc.MysqlDataSource();
		try {
			reader = new FileInputStream(System.getProperty("user.dir")+"\\EMSAdress.properties");
			if(reader.read() != -1) {
				if(!load()) {
					configured = false;
				} else {
					configured = true;
					
				}
			} else {
				configured = false;
			}
		} catch (FileNotFoundException e) {
			configured = false;
		} catch (IOException e) {
			configured = false;
		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				
			}
		}
}

@Override
public void saveAttendance(int timerMinutes,boolean timerStarted) {
	if(timerStarted) {
		try {
			PreparedStatement prepared = conn.prepareStatement("INSERT into attendeddates (ID,Date,Minutes) VALUES (?,?,?)");
			prepared.setInt(1,credentials.currentId());
			prepared.setDate(2,new Date(System.currentTimeMillis()));
			prepared.setInt(3,timerMinutes);
			SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery("SELECT NOW()");
			result.next();
			String serverTime = result.getString(1);
			java.util.Date serverDate = converter.parse(serverTime);
			prepared.executeUpdate();



		} catch (SQLException e) {
			printer.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		} catch (ParseException e) {
			printer.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);

		}
	}
}

public boolean isConfigured() {
	return configured;
}


public boolean setupDB(String address,String userName,String passWord) {

	String DBCreate = "CREATE TABLE IF NOT EXISTS employeedata ("
			+ "name TINYTEXT NOT NULL,"
			+ "id MEDIUMINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,"
			+ "passwrd TINYTEXT NOT NULL,"
			+ "`rank` TINYINT NOT NULL,"
			+ "score TINYINT NOT NULL) ENGINE = INNODB";
	String dateDBCreate = "CREATE TABLE IF NOT EXISTS attendeddates("
			+ "id TINYTEXT NOT NULL,"
			+ "date DATE NOT NULL,"
			+ "minutes TINYINT NOT NULL) ENGINE = INNODB";

	try(FileWriter writer = new FileWriter(new File("EMSAdress.properties"))){
		Properties config = new Properties();
		String username =  userName;
		String password =  passWord;		
		//config.setProperty("mysql.driver","com.mysql.jdbc.Driver");
		//config.setProperty("mysql.url","jdbc:mysql://" + address +"/")
		String testAddress = "jdbc:mysql://" + address + "/?serverTimezone=UTC";
		address = "jdbc:mysql://" + address + "/emsdatabase?serverTimezone=UTC";
		ds.setURL(testAddress);
		ds.setUser(username);
		ds.setPassword(password);
		config.setProperty("mysql.url",address);
		config.setProperty("mysql.username",username);
		config.setProperty("mysql.password",password);
		conn = ds.getConnection();
		stmt = conn.createStatement();
		conn.setAutoCommit(false);
		config.store(writer,"Mysql properties");
		//TODO set parameters

		if(conn.isValid(0)) {
			printer.printInfo("Connection successful! Now the database will be created.");
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS emsdatabase");
			stmt.executeUpdate("use emsdatabase");
			stmt.executeUpdate(DBCreate);
			stmt.executeUpdate(dateDBCreate);
			printer.printInfo("Done! you may now use this software as normal.");
			return true;
		}
	} catch (SQLException e) {
		return false;
	} catch (IOException e) {
		return false;
	}
	return false;
}




@Override
public boolean setPerformance(int id, int score) {
	if(getRank(credentials.currentId()) > getRank(id)) {
		String query = "UPDATE employeedata set Score = " + Integer.toString(score) + " WHERE ID = " + Integer.toString(id);
		try {
			stmt.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			printer.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
			return false;
		}
	} else {
		return false;
	}


}

@Override
public int setNewEmployee(String passWord, String name,int rank) {
	String query = "INSERT INTO employeedata(name,passwrd,`rank`) VALUES (";
	String values = "'" + name +  "'" + "," + "'" + passWord + "'" + "," + Integer.toString(rank) +
			")";
	try {
		stmt.executeUpdate(query + values);
		ResultSet rs = stmt.executeQuery("Select ID from employeedata where name ='" + name +"'");
		rs.next();
		return rs.getInt(1);
	} catch (SQLException e) {
		printer.printInfo("An exception occured. Please check the logFile for more info.");
		logFiler.saveStackTrace(e);
		return -1;
	}

}

@Override
public boolean setRank(int id, int rank) {
	if(getRank(credentials.currentId()) > 4) {
		String query = "UPDATE employeedata set Rank = " + Integer.toString(rank) + "WHERE ID = " + Integer.toString(id);
		try {
			stmt.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			printer.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
			return false;
		}
	} else {
		return false;
	}
}




@Override
public int getRank(int id) {
	String query = "Select `Rank` from employeedata"
			+ " Where ID =(" + Integer.toString(id)
			+")";
	try {
		ResultSet rankSet = stmt.executeQuery(query);
		rankSet.next();
		return rankSet.getInt(1);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	printer.printInfo("Rank not found. Returning 0. Contact your supervisor.");
	return 0;

}

@Override
public ArrayList<Integer> getPerformance(int userId,int id) {
	if(getRank(userId) > getRank(id) || userId == id) {
		String query = "Select Score from attendeddates "
				+ "Where ID =(" + Integer.toString(id) + ")";
		try {
			ResultSet scoreSet = stmt.executeQuery(query);
			ArrayList<Integer> result = new ArrayList<Integer>();
			while(scoreSet.next()) {
				result.add(scoreSet.getInt(1));
			}
			return result;
		} catch (SQLException e) {
			printer.printInfo("An exception occured. Please check the logFile for more info. Press Enter to exit.");
			logFiler.saveStackTrace(e);

			System.exit(-2);
			return null;
		}
	} else {
		return null;
	}

}


@Override
public ArrayList<String> returnAttendance(int userId,int id, int year, int month) {
	if(getRank(userId) > getRank(id) || userId == id) {
		try {
			PreparedStatement dateStatement = conn.prepareStatement("Select date from attendeddates WHERE YEAR(date) = ? AND MONTH(date) = ? AND "
					+ "ID = ?");
			dateStatement.setInt(1, year);
			dateStatement.setInt(2, month);
			dateStatement.setInt(3, id);
			dateStatement.executeQuery();
			ResultSet attendanceSet = dateStatement.getResultSet();
			ArrayList<String> dates = new ArrayList<String>();
			while(attendanceSet.next()) {
				String addedDate = attendanceSet.getDate(1).toString();
				dates.add(addedDate);
			}
			PreparedStatement timeStatement = conn.prepareStatement("Select minutes from attendeddates WHERE date = ? AND ID = ?");
			ArrayList<String> returnable = new ArrayList<String>();
			for(String date : dates) {
				java.time.Instant dateInMilli = java.time.Instant.parse(date + "T00:00:00.00Z");
				
				timeStatement.setDate(1, new java.sql.Date(dateInMilli.toEpochMilli()));
				timeStatement.setInt(2, id);
				ResultSet timeSet = timeStatement.executeQuery();
				int minutesWorked = 0;
				if(timeSet.next()) {
					minutesWorked = timeSet.getInt(1);
				} else {
					minutesWorked = 0;
				}
				returnable.add(date + " Minutes: " + String.valueOf(minutesWorked));
			}
			

			return returnable;
		} catch (SQLException e) {
			printer.printInfo("An exception occured. Please check the logFile for more info. Press Enter to exit.");
			logFiler.saveStackTrace(e);
			System.exit(-2);
			return new ArrayList<String>();

			
		}
	} else {
		return new ArrayList<String>();
	}

}

@Override
public String getName(int id) {
	String Query = "select Name from employeedata where ID =" + Integer.toString(id);
	try {
		ResultSet nameSet = stmt.executeQuery(Query);
		nameSet.next();
		return nameSet.getString(1);
	} catch (SQLException e) {
		printer.printInfo("An exception occured. Please check the logFile for more info. Press Enter to exit.");
		logFiler.saveStackTrace(e);

		System.exit(-2);
	}
	printer.printInfo("name not found. Returning nothing. Contact your supervisor.");
	return "";
}

@Override
public ArrayList<String> returnNames() {
	String query = "Select Name from employeedata Where `Rank` <= " + Integer.toString(getRank(credentials.currentId()));
	try {
		stmt.execute(query);
		ResultSet nameSet = stmt.getResultSet();
		ArrayList<String> returnable = new ArrayList<String>();
		int i = 0;
		while(nameSet.next()) {
			i++;
			returnable.add(nameSet.getString(1));
		}
		return returnable;
	} catch (SQLException e) {
		printer.printInfo("An exception occured. Please check the logFile for more info. Press Enter to exit.");
		logFiler.saveStackTrace(e);

		System.exit(-2);
	}
	printer.printInfo("Names not found. Returning null. Contact your supervisor.");
	return null;
}

@Override
public ArrayList<String> returnIDs() {
	String query = "Select ID from employeedata Where `Rank` <= " + Integer.toString(getRank(credentials.currentId()));
	try {
		stmt.execute(query);
		ResultSet idSet = stmt.getResultSet();
		ArrayList<String> returnable = new ArrayList<String>();
		int i = 0;
		while(idSet.next()) {
			i++;
			returnable.add(Integer.toString(idSet.getInt(1)));
		}
		return returnable;
	} catch (SQLException e) {
		printer.printInfo("An exception occured. Please check the logFile for more info. Press Enter to exit.");
		logFiler.saveStackTrace(e);

		System.exit(-2);
	}
	printer.printInfo("IDs not found. Returning null.Contact your supervisor.");
	return null;
}

@Override
public boolean save() {
	try {
		conn.commit();
		return true;
	} catch (SQLException e) {
		logFiler.saveStackTrace(e);
		return false;
	}
}

public Connection returnConnection() {
	return conn;
}

public boolean load() {
	try (FileInputStream configreader = new FileInputStream(System.getProperty("user.dir")+"\\EMSAdress.properties")){
		configreader.read();
		config.load(configreader);
		com.mysql.cj.jdbc.MysqlDataSource ds = new com.mysql.cj.jdbc.MysqlDataSource();
		ds.setURL(config.getProperty("mysql.url"));
		ds.setUser(config.getProperty("mysql.username"));
		ds.setPassword(config.getProperty("mysql.password"));
		try {
			conn = ds.getConnection();
		} catch (Exception e) {
			printer.printInfo("Something went wrong when connecting to the server. Check if the server is online.\n"
					+ "If it is, please contact the developer. Press Enter to exit.");

			System.exit(-1);
		}
		conn.setAutoCommit(false);
		stmt = conn.createStatement();
		return true;
	} catch (SQLException e) {
		logFiler.saveStackTrace(e);
		printer.printInfo("Something went wrong when connecting to the server. Check if the server is online.\n"
				+ "If it is, please contact the developer. Press Enter to exit.");

		return false;
	} catch (FileNotFoundException e1) {
		logFiler.saveStackTrace(e1);
		printer.printInfo("Something went wrong with the configfile. Please contact the developer. Press Enter to exit.");

		return false;
	} catch (IOException e1) {
		logFiler.saveStackTrace(e1);
		printer.printInfo("Something went wrong with the configfile. Please contact the developer. Press Enter to exit.");

		return false;
	}

}

@Override
public void getDependencies(Launcher launch) {
	printer = launch.returnPrint();
	credentials = launch.returnLogin();
	logFiler = launch.returnLogFiler();
	
}

}