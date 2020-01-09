package ems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


class Base64Operator{ 
	Base64.Decoder decoder = Base64.getDecoder();
	Base64.Encoder encoder = Base64.getEncoder();

	public byte[] padByte(byte[] malformed) {
		int offset = malformed.length % 3;
		ArrayList<Byte> padding = new ArrayList<Byte>();
		ArrayList<Byte> temp = new ArrayList<Byte>();
		for(byte b:malformed) {
			temp.add(b);
		}
		for(int i=0;i < offset;i++) {
			padding.add((byte) 0);
		}
		temp.addAll(padding);
		byte[] padded = new byte[temp.size()];
		for(int i=0;i < padding.size();i++) {
			padded[i] = temp.get(i).byteValue();
		}
		return padded;
	}

	public byte[] decode(byte[] array) {
		return decoder.decode(array);
	}

	public byte[] encode(byte[] input) {
		return encoder.encode(input);
	}

	public byte[] encode(String input) {
		byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
		return encoder.encode(bytes);
	}
}




class textBasedBrowser implements Browser{
	Printer print;
	EMSDataHandler handler;
	Login log;
	Date startTime;
	LogFiler logFiler;
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	int currentId = 0;
	int selectedId = 0;
	int currentDate = 0;
	boolean quitReq = false;


	public void waitInput() {
		print("Press enter to continue.");
		nextLine();
	}

	public void waitCommands() {
		while(!quitRequested()) {
			print("Please select an command. Type help for a list of commands.");
			String nextCommand = nextLine().toLowerCase();
			switch(nextCommand) {
			case "select":
				print("Please input the id");
				try {
					int id = Integer.parseInt(nextLine());
					selectEmployee(id);
				} catch (NumberFormatException e) {
					print("Please input only numbers with no spaces or dashes.");
					break;
				}
				break;
			case "getscore":
				if(selectedId() == 0) {
					print("Please select an employee with the select command first.");
					break;
				}
				getScore();
				break;
			case "setscore":
				if(selectedId() == 0) {
					print("Please select an employee with the select command first.");
					break;
				}
				print("input the score.");
				try {
					int score = Integer.parseInt(nextLine());
					setScore(score);
				} catch (NumberFormatException e) {
					print("Please input a score from 0 to 10.");
					break;
				}
				break;
			case "starttimer":
				startAttendanceTimer();
				break;
			case "setrank":
				if(selectedId() == 0) {
					print("Please select an employee with the select command first.");
					break;
				}
				print("input the rank.");
				try {
					int rank = Integer.parseInt(nextLine());
					setRank(rank);
				} catch (NumberFormatException e) {
					print("Please input a rank from 1 to 5.");
					break;
				}
				break;
			case "add":
				try {
					if(!isSetup() && handler.getRank(returnBrowserId()) < 2) {
						print("This command is only available to rank 2 or higher.");
						break;
					}
				} catch (IOException | SQLException e1) {
					logFiler.saveStackTrace(e1);
					print("Something went wrong when saving. Please check the log file.");
					print("Press Enter to exit.");
					nextLine();
				}
				print("input the name.");
				String name = nextLine();
				print("input the password.");
				String passWord;
				if(System.console() != null) {
					passWord = readPassword();
				} else {
					print("This device does not support echo disabling. Please note that the password will be displayed on screen.");
					passWord = nextLine();

				}
				String compare;
				print("Input it again to confirm.");
				if(System.console() != null) {
					compare = readPassword();
				} else {
					print("This device does not support echo disabling. Please note that the password will be displayed on screen.");
					compare = nextLine();

				}
				if(!passWord.equals(compare)) {
					print("The two passwords does not match. Please try again.");
					break;
				}
				print("input the rank.");
				String rank = nextLine();
				addEmployee(passWord, name,Integer.parseInt(rank));

				break;
			case "quit":
				try {
					saveAndQuit();
				} catch (IOException | SQLException e) {
					logFiler.saveStackTrace(e);
					print("Something went wrong when saving. Please check the log file.");
					print("Press Enter to exit.");
					nextLine();
				}
				requestQuit();
				break;
			case "help":
				print("the current supported commands are:");
				print("select,setrank,starttimer,setscore,add,quit, and getscore.");
				break;
			default:
				print("That input is invalid. the current supported commands are:");
				print("select,setrank,starttimer,setscore,add,quit, and getscore.");

			}

		}
	}



	public void loginProceedure() {
		boolean logged = false;
		while(!logged) {
			print("Please input your id.");
			String inputtedId = nextLine();
			boolean allNums = true;
			for(char c:inputtedId.toCharArray()) {
				if(!Character.isDigit(c)) {
					allNums = false;
				}
			}
			if(allNums == false) {
				print("IDs are all numbers. Please try again.");
				continue;
			}
			int id = Integer.parseInt(inputtedId);
			print("Please input your Password.");
			char[] passWord;
			if(System.console() != null) {
				passWord = readPassword().toCharArray();
			} else {
				print("This device does not support echo disabling. Please note that the password will be displayed on screen.");
				passWord = nextLine().toCharArray();

			}
			if(login(id, passWord) == false) {
				print("Credentials invalid. Please try again.");
			} else {
				logged = true;
			}
		}
	}

	public boolean timerStarted() {
		if(startTime != null) {
			return true;
		} else {
			return false;
		}
	}


	public void saveAndQuit() throws IOException,SQLException{
		handler.saveAttendance(returnTimerMinutes(),timerStarted());
		handler.save();

	}
	public int diffFromStart(Date date) {
		long milliStart = startTime.getTime();
		long milliNow = date.getTime();
		long difference = milliNow - milliStart;
		int MinuteDiff = (int) TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS);
		return MinuteDiff;
	}

	public int selectedId() {
		return selectedId;
	}

	public boolean login(int id,char[] passWord) {
		boolean result = log.userIsValid(id, passWord);
		if(result) {
			currentId = log.currentId();
			print("Access granted.");
		}

		return result;
	}

	public String readPassword() {	
		return new String(System.console().readPassword());
	}

	public String nextLine() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			logFiler.saveStackTrace(e);
	
		}
		return null;
	}

	public boolean isSetup() {
		if(this.returnBrowserId() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public void setRank(int rank) {
		if(handler.setRank(selectedId, rank) == true) {
			print("Rank modification succesful");
		} else {
			print("An error occured. please check the input and try again.");
		}
	}

	public void print(String s) {
		print.printInfo(s);
	}

	public void showEmpTable() {
		if(!handler.returnNames().isEmpty()) {
			print("------------------------");
			print("NAME       |ID ");
			for(int nLen = 0; nLen < handler.returnNames().size(); nLen++) {
				print(handler.returnNames().get(nLen) + "       " + 
						handler.returnIDs().get(nLen));
			}
			print("------------------------");
		}
	}

	public void selectEmployee(int id) {
		selectedId = id;
		print("ID:" + String.valueOf(selectedId) + " selected.");
	}

	public void getScore(){
		int rank;
		try {
			rank = handler.getRank(currentId);
			if(rank > 2) {
				ArrayList<Integer> pf = handler.getPerformance(currentId,selectedId);
				StringBuilder sb = new StringBuilder();
				sb.append("Performance score over 7 days of ID:");
				sb.append(String.valueOf(selectedId));
				sb.append(" is: ");
				boolean indexOver = false;
				for(int i=0;i<7;i++) {
					if(i > pf.size()) {
						indexOver = true;
					}
					if(!indexOver) {
						sb.append(pf.get(i));
						sb.append(",");
					} else {
						sb.append("None");
					}
				}
				print(sb.toString());
			} else {
				print("Your rank is too low. Only rank 2 and higher"
						+ " has access to this.");
			}
		} catch (IOException | SQLException e) {
			print("Something went wrong when getting the score. Please check the log file.");
			print("Press Enter to exit.");
			nextLine();
		}
	}

	public void setScore(int score) {
		if(handler.setPerformance(selectedId, score)) {
			print("Set score of Id:" + String.valueOf(selectedId) + 
					" to " + String.valueOf(score));
		} else {
			print("An error occured. Please check your rank and if the score you chose "
					+ "was less than 10.");
		}
	}


	public void requestQuit() {
		quitReq = true;
	}

	public int returnBrowserId(){
		return currentId;
	}

	public LocalDateTime currentDate() {
		return LocalDateTime.now();
	}

	public boolean quitRequested() {
		return quitReq;
	}
	@Override
	public void getAttendance(String yearAndMonth) {
		Calendar currentDate = Calendar.getInstance();
		int month =Integer.parseInt(yearAndMonth.substring(4));
		int year = Integer.parseInt(yearAndMonth.substring(0, 4));
		try {
			handler.returnAttendance(currentId, selectedId, year, month);
		} catch (IOException e) {
			logFiler.saveStackTrace(e);
		} catch (SQLException e) {
			logFiler.saveStackTrace(e);
		}

	}
	@Override
	public void startAttendanceTimer() {
		Calendar timer = Calendar.getInstance();
		startTime = timer.getTime();
		print("The timer has been started. Use the exit command to end the timer.");

	}
	@Override
	public int returnTimerMinutes() {
		long milliStart = startTime.getTime();
		long milliNow = Calendar.getInstance().getTimeInMillis();
		long difference = milliNow - milliStart;
		int MinuteDiff = (int) TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS);
		return MinuteDiff;
	}
	@Override
	public void addEmployee(String passWord, String name, int rank) {
		int returnedID = handler.setNewEmployee(passWord, name,rank);
		if(returnedID != -1) {
			print("Succesfully added: \n"
					+ "ID:" + returnedID +
					"\nName:" + name);
		} else {
			print("An error has occured and the registration was unsuccesful. "
					+ "Please contact the developer.");
		}
	}

	@Override
	public void getDependencies(Launcher launch) {
		print = launch.returnPrint();
		handler = launch.returnHandler();
		log = launch.returnLogin();
		logFiler = launch.returnLogFiler();
		
		
	}

	@Override
	public String printAndGetInput(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initiate() {
		loginProceedure();
		waitInput();
		
	}
}





