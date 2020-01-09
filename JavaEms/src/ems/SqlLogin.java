package ems;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class SqlLogin implements Login {
	EMSDataHandler get;
	Connection conn;
	Statement stmt;
	LogFiler logFiler; 
	int currentId = -1;


	public void initiate() {
		conn = ((SqlDataHandler) get).returnConnection();
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			logFiler.saveStackTrace(e);
		}
	}


	@Override
	public boolean userIsValid(int id, char[] Password) {
		if(id == 12345 && String.valueOf(Password).equals("setup")) {
			if(get.returnIDs().size() == 0) {
				return true;
			} else {
				return false;
			}
		}
		String query = "SELECT passwrd FROM employeedata WHERE ID = " + Integer.toString(id);
		String check = "SELECT COUNT(passwrd) FROM employeedata";
		try {
			ResultSet checkResult = stmt.executeQuery(check);
			checkResult.next();
			if(checkResult.getInt(1) == 0) {
				currentId = id;
				return true;
			} else {
				ResultSet result = stmt.executeQuery(query);
				if(result.next()) {
					char[] fromDB = result.getString(1).toCharArray();
					if(Arrays.equals(fromDB, Password)) {
						currentId = id;
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			logFiler.saveStackTrace(e);
			System.exit(-2);


		}
		return false;
	}

	@Override
	public int currentId() {
		return currentId;
	}




	@Override
	public void getDependencies(Launcher launch) {
		get = launch.returnHandler();
		logFiler = launch.returnLogFiler();


	}

}
