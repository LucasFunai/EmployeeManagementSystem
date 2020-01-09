package ems.FXEms;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import ems.Browser;
import ems.EMSDataHandler;
import ems.Launcher;
import ems.LogFiler;
import ems.Login;
import ems.Printer;
import ems.Setter;
import ems.TextLogFiler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ems.FXEms.ObservableIDPair;

public class FXBasedBrowser extends Application implements Browser {

	Printer print;
	Setter set;
	EMSDataHandler dataHandle;
	Login log;
	LogFiler logFiler;
	Date startTime;
	int currentId = 0;
	int selectedId = 0;
	int currentDate = 0;
	LoginLoader loginControl;
	Scene thisScene;
	ObservableList<ObservableIDPair> tuples;
	final NumberAxis xAxis = new NumberAxis();
	final NumberAxis yAxis = new NumberAxis();

	public FXBasedBrowser(){

	}
	@FXML
	private DatePicker attendanceDatePicker;

	@FXML
	private BarChart<String,Integer> workTimeChart;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button SetRankButton;

	@FXML
	private Button StartTimerButton;

	@FXML
	private Button SetScoreButton;

	@FXML
	private Button AddButton;

	@FXML
	private Button QuitButton;

	@FXML
	private Font x1;

	@FXML
	private Color x2;

	@FXML
	private TableView<ObservableIDPair> emsTableView;

	@FXML
	private TableColumn<ObservableIDPair, String> TableNames;

	@FXML
	private TableColumn<ObservableIDPair, Integer> TableIDs;

	@FXML
	private LineChart<Integer, Integer> ScorePerDayChart;

	@FXML
	private PieChart AttendedPercentChart;

	@FXML
	private Label CurrentPermission;

	@FXML
	private Font x3;

	@FXML
	private Color x4;
	
	@FXML
	private Label scoreDeniedText;
	
	@FXML
	private Label worktimeDeniedText;

	@FXML
	private Label ErrorLog;
	private boolean quitRequest;



	@FXML
	void AddPressed(ActionEvent event) {
		try {
			if(dataHandle.getRank(currentId) == 5) {
				String name = print.printAndGetInput("First, input the name of the new employee.");
				int rank = Integer.parseInt(print.printAndGetInput("Now, the rank in a scale of 1 - 5. Please refer to the manual"
						+ " for the description of each rank."));
				if(rank > 5 || rank < 1) {
					throw new NumberFormatException("Rank was bigger than 5, or lower than 1.");
				}
				String passWord = print.printAndGetInput("At last, the password.");
				this.addEmployee(passWord, name, rank);
			}
		} catch (IOException e) {
			print.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		} catch (SQLException e) {
			print.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		} catch (NumberFormatException e) {
			print.printInfo("The rank you inputted is not valid. Please check if you only used numbers and try again.");
			return;
		}


	}

	@FXML
	void QuitPressed(ActionEvent event) {
		this.requestQuit();

	}



	@FXML
	void ScoreChartChangeRange(MouseEvent event) {

	}

	@FXML
	void SetRankPressed(ActionEvent event) {
		int rank = Integer.parseInt(print.printAndGetInput("Please input the rank."));
		this.setRank(rank);
		

	}

	@FXML
	void SetScorePressed(ActionEvent event) {
		int score = Integer.parseInt(print.printAndGetInput("Please input your daily rating of this employee."));
		this.setScore(score);
	}

	@FXML
	void StartTimerPressed(ActionEvent event) {
		this.startAttendanceTimer();
		this.StartTimerButton.setText("Started!");
		this.StartTimerButton.setDisable(true);
	}

	public void initiate(Launcher launch) {
		getDependencies(launch);
	}

	void setupDB() {

	}

	@FXML
	void initialize() {
		if(dataHandle.isConfigured()) {
			try {
				dataHandle.load();
			} catch (IOException e) {
				print("Something went wrong when reading the file. Please check the log file.");
				System.exit(-5);
			} catch (SQLException e) {
				print("No response from SQL server. Please check the log file.");
				System.exit(-5);
			}
		} else {
			print("No Address found. Please set the parameters in the config file.");
			System.exit(-5);
		}
		attendanceDatePicker.setVisible(false);
		

		emsTableView.setOnMousePressed(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				int id = 0;
				try {
				id = emsTableView.getSelectionModel().getSelectedItem().getId();
				} catch (NullPointerException e) {
					return;
				}
				selectEmployee(id);
				getScore();
				SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM");
				Date date = new Date(System.currentTimeMillis());
				attendanceDatePicker.setVisible(true);
				getAttendance(formatter.format(date));

			}
		});
		attendanceDatePicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				LocalDate date = attendanceDatePicker.getValue();
				DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM");
				getAttendance(date.format(formatter));
			}
		});



	}

	@Override
	public boolean login(int id, char[] passWord) {
		return log.userIsValid(id, passWord);
	}

	@Override
	public void loginProceedure() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
		Parent root;
		try {
			root = loader.load();
			loginControl = loader.getController();
			loginControl.getDependenciesFromBrowser(this);
			Stage loginStage = new Stage();
			loginStage.setScene(new Scene(root));
			loginControl.initialize();
			loginControl.SubmitButton.setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
					loginControl.LoginRequest(event);

				}

			}		
					);
			loginStage.showAndWait();
			if(loginControl.returnLoggedId() == -1) {
				Platform.exit();
				System.exit(0);
			}

		} catch (IOException e) {
			print.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		}


	}

	@Override
	public void waitCommands() {
		//Interface requirements

	}

	@SuppressWarnings("unchecked")
	@Override
	public void showEmpTable() {
		emsTableView.getItems().clear();
		ArrayList<String> names = dataHandle.returnNames();
		ArrayList<String> ids = dataHandle.returnIDs();
		tuples = FXCollections.observableArrayList();
		for(int i=0;i < ids.size();i++) {
			tuples.add(new ObservableIDPair(names.get(i),Integer.parseInt(ids.get(i))));
		}
		TableNames.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableIDs.setCellValueFactory(new PropertyValueFactory<>("id"));
		emsTableView.getColumns().clear();
		emsTableView.getColumns().addAll(TableNames,TableIDs);
		emsTableView.getItems().setAll(tuples);
	}

	@Override
	public int diffFromStart(Date date) {
		long milliStart = startTime.getTime();
		long milliNow = date.getTime();
		long difference = milliNow - milliStart;
		int MinuteDiff = (int) TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS);
		return MinuteDiff;
	}

	@Override
	public String readPassword() {
		//Also does nothing. readPassword() already included in loginProcedure.
		return "";
	}

	@Override
	public int selectedId() {
		return selectedId;
	}

	@Override
	public String printAndGetInput(String s) {
		return print.printAndGetInput(s);
	}

	@Override
	public void print(String s) {
		print.printInfo(s);

	}

	@Override
	public void selectEmployee(int id) {
		//Make clicking an ID in the tableView call this. <- done
		selectedId = id;
	}

	@Override
	public void getScore() {
		//Make this be called when an employee is selected, and change the graphics in the right. <- done
		try {
			ScorePerDayChart.getData().clear();
			ArrayList<Integer> scoreSet = dataHandle.getPerformance(currentId, selectedId);
			if(scoreSet == null) {
				ScorePerDayChart.setVisible(false);
				scoreDeniedText.setVisible(true);
				return;
			}
			ScorePerDayChart.setVisible(true);
			scoreDeniedText.setVisible(false);
			XYChart.Series<Integer,Integer> series = new XYChart.Series<>();
			Integer index = 0;
			for(Integer i : scoreSet) {
				index++;
				series.getData().add(new XYChart.Data<Integer,Integer>(index,i));
			}
			ScorePerDayChart.setTitle("Score graph of : " + dataHandle.getName(selectedId));
			ScorePerDayChart.getData().add(series);
		} catch (IOException e) {
			print.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		} catch (SQLException e) {
			print.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		}

	}

	@Override
	public void setScore(int score) {
		try {
			if(dataHandle.getRank(currentId) > 1) {
				set.setPerformance(selectedId, score);
			}
		} catch (IOException | SQLException e) {
			print("Something went wrong when setting score. Please check the log file.");
			printAndGetInput("Press Enter to exit.");

		}

	}

	@Override
	public void addEmployee(String passWord, String name, int rank) {
		//TODO this has to be called after clicking add, and inputting all adequate info.
		int id = dataHandle.setNewEmployee(passWord, name, rank);
		if(id < 0) {
			print.printInfo("Something went wrong. Please check if your rank is high enough.");
		}
		print.printInfo("Success! the ID is: " + String.valueOf(id) + " PLEASE DO NOT LOSE THIS ID.");
	    this.showEmpTable();


	}

	@Override
	public void startAttendanceTimer() {
		Calendar timer = Calendar.getInstance();
		startTime = timer.getTime();
		print.printInfo("The timer has been started.");

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
	public void getAttendance(String yearAndMonth) {
		try {
			
			
			workTimeChart.getData().clear();
			//Format: yyyy-MM
			int year = Integer.parseInt(yearAndMonth.substring(0, 4));
			int month = Integer.parseInt(yearAndMonth.substring(5,7));
			ArrayList<String> dateAndTime = dataHandle.returnAttendance(currentId, selectedId, year,month);
			if(dateAndTime == null) {
				workTimeChart.setVisible(false);
				worktimeDeniedText.setVisible(true);
				return;
			}
			workTimeChart.setVisible(true);
			worktimeDeniedText.setVisible(false);
			XYChart.Series<String,Integer> series = new XYChart.Series<>();
			SimpleDateFormat formatter= new SimpleDateFormat("dd");
			Date date = new Date(System.currentTimeMillis());
			byte today = Byte.parseByte(formatter.format(date));
			ArrayList<String> hackaroundCategoryNameList = new ArrayList<String>();
			int lengthOfMonth = YearMonth.of(year, month).lengthOfMonth();
			for(int i=1; i < lengthOfMonth;i++) {
				hackaroundCategoryNameList.add(String.valueOf(i));
				for(String s : dateAndTime) {
					byte day = Byte.parseByte(s.substring(8,10));
					if(day == i) {
						int minutes = Byte.parseByte(s.substring(20));
						series.getData().add(new XYChart.Data<>(String.valueOf(day),minutes));
					}
				}
			}




			workTimeChart.setTitle("Attendance graph of : " + dataHandle.getName(selectedId));
			CategoryAxis xAxis = (CategoryAxis)workTimeChart.getXAxis();
			xAxis.setCategories(FXCollections.observableArrayList(hackaroundCategoryNameList));
			workTimeChart.getData().addAll(series);

		} catch (IOException e) {
			print.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		} catch (SQLException e) {
			print.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		}



	}

	@Override
	public int returnBrowserId() {
		return currentId;
	}

	@Override
	public void saveAndQuit() {
		try {
			dataHandle.save();
			System.exit(0);
		} catch (IOException e) {
			print.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		} catch (SQLException e) {
			print.printInfo("An exception occured. Please check the logFile for more info.");
			logFiler.saveStackTrace(e);
		}

	}

	@Override
	public boolean timerStarted() {
		return Boolean.valueOf(startTime != null);
	}

	@Override
	public LocalDateTime currentDate() {
		return LocalDateTime.now();
	}

	@Override
	public boolean isSetup() {
		if(this.dataHandle.returnNames().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setRank(int rank) {
		if(dataHandle.setRank(selectedId, rank)) {
			print.printInfo("Succesfully changed!");
		} else {
			print.printInfo("Operation failed. Check if you have the required rank.");
		}
	}

	@Override
	public void requestQuit() {
		char input = Character.toLowerCase(print.printAndGetInput("Are sure you want to quit? Input Y to save and quit, N to return, Q to quit without saving.").charAt(0));
		quitRequest = true;

		if(input == (char)'y') {
			this.saveAndQuit();
		} else if(input == (char)'n') {
			return;
		} else if(input == (char)'q') {
			System.exit(0);
		} else {
			print.printInfo("Invalid input. please try again.");
			return;
		}


	}

	@Override
	public boolean quitRequested() {
		return quitRequest;
	}

	@Override
	public void waitInput() {
		//Nothing. Was just made to not anger my interface.

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("EMSInterface.fxml"));
		emsTableView = new TableView<>();
		loader.setController(this);
		Parent root = loader.load();
		this.loginProceedure();
		this.currentId = log.currentId();
		primaryStage.setTitle("EMS");
		thisScene = new Scene(root,600,400);
		primaryStage.setScene(thisScene);
		primaryStage.show();
		this.showEmpTable();
		//Second browser problem fixed.

	}

	@Override
	public void getDependencies(Launcher launch) {
		print = launch.returnPrint();
		dataHandle = launch.returnHandler();
		log = launch.returnLogin();
		logFiler = launch.returnLogFiler();
	}
	
	@Override
	public void initiate() {
		//Not needed. This is an FX application, but I had to add this to override all methods from the interface.
	}
}
