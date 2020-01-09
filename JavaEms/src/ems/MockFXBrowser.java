package ems;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import ems.FXEms.LoginLoader;
import javafx.application.Application; 
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

//Saved for backup. This was sample version of FXBrowser made to troubleshoot the tableView.

public class MockFXBrowser extends Application implements Browser{
	
	static Printer print;
	static Setter set;
	static EMSDataHandler get;
	static Login log;
	Date startTime;
	int currentId = 0;
	int selectedId = 0;
	int currentDate = 0;
	Scene thisScene;
	ObservableList<ObservableIDPair> tuples;

	public MockFXBrowser(){

	}




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
	private LineChart<?, ?> ScorePerDayChart;

	@FXML
	private PieChart AttendedPercentChart;

	@FXML
	private PieChart AverageScoreChart;

	@FXML
	private Label CurrentPermission;

	@FXML
	private Font x3;

	@FXML
	private Color x4;

	@FXML
	private Label ErrorLog;
	private boolean quitRequest;



	@FXML
	void AddPressed(ActionEvent event) {

	}

	@FXML
	void QuitPressed(ActionEvent event) {

	}

	@FXML
	void ScoreChartChangeRange(MouseEvent event) {

	}

	@FXML
	void SetRankPressed(ActionEvent event) {

	}

	@FXML
	void SetScorePressed(ActionEvent event) {

	}

	@FXML
	void StartTimerPressed(ActionEvent event) {

	}


	@Override
	public boolean login(int id, char[] passWord) {
		//Normally takes result from SqlLogin.
		return true;
	}

	@Override
	public void loginProceedure() {
		//Normally opens a window for login.
		
	}

	@Override
	public void waitCommands() {
		//Was very needed in the text version, but lost its meaning in the FXBrowser.
		
	}

	@Override
	public void showEmpTable() {
		//Lets see if this works.
		//Commented out the part where it uses sql to retrieve data.
		//ArrayList<String> names = get.returnNames();
		//ArrayList<String> ids = get.returnIDs();
		ArrayList<String> names = new ArrayList<String>();
		names.add("John Doe");
		ArrayList<String> ids = new ArrayList<String>();
		ids.add("1");
		tuples = FXCollections.observableArrayList();
		for(int i=0;i < ids.size();i++) {
			tuples.add(new ObservableIDPair(names.get(i),Integer.parseInt(ids.get(i))));
		}
		TableNames = new TableColumn("Names");
		TableNames.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableIDs = new TableColumn("IDs");
		TableIDs.setCellValueFactory(new PropertyValueFactory<>("id"));
		emsTableView.getColumns().addAll(TableNames,TableIDs);
		emsTableView.getItems().setAll(tuples);

	}

	void initialize() {
		  assert SetRankButton != null : "fx:id=\"SetRankButton\" was not injected: check your FXML file 'EMSInterface.fxml'.";
	        assert StartTimerButton != null : "fx:id=\"StartTimerButton\" was not injected: check your FXML file 'EMSInterface.fxml'.";
	        assert SetScoreButton != null : "fx:id=\"SetScoreButton\" was not injected: check your FXML file 'EMSInterface.fxml'.";
	        assert AddButton != null : "fx:id=\"AddButton\" was not injected: check your FXML file 'EMSInterface.fxml'.";
	        assert QuitButton != null : "fx:id=\"QuitButton\" was not injected: check your FXML file 'EMSInterface.fxml'.";
	        assert ScorePerDayChart != null : "fx:id=\"ScorePerDayChart\" was not injected: check your FXML file 'EMSInterface.fxml'.";
	        assert AttendedPercentChart != null : "fx:id=\"AttendedPercentChart\" was not injected: check your FXML file 'EMSInterface.fxml'.";
	        assert AverageScoreChart != null : "fx:id=\"AverageScoreChart\" was not injected: check your FXML file 'EMSInterface.fxml'.";
	        
		Platform.setImplicitExit(false);


	}
	
	@Override
	public int diffFromStart(Date date) {
		//Normally calculates the difference from a stored date to the inputted date.
		return 0;
	}

	@Override
	public String readPassword() {
		//Method to hide the password while its being typed.
		return null;
	}

	@Override
	public int selectedId() {
		//Normally returns the ID that is clicked in the table.
		return 0;
	}

	@Override
	public String nextLine() {
		//Needed in text version. Not much here.
		return null;
	}

	@Override
	public void print(String s) {
		//Opens a window showing the message.
		
	}

	@Override
	public void selectEmployee(int id) {
		//Literally select an id.
		
	}

	@Override
	public void getScore() {
		//Get stored employee performance score.
		
	}

	@Override
	public void setScore(int score) {
		//set score.
		
	}

	@Override
	public void addEmployee(String passWord, String name, int rank) {
		//add employee.
		
	}

	@Override
	public void startAttendanceTimer() {
		//Start timer to measure work time.
		
	}

	@Override
	public int returnTimerMinutes() {
		//Self explanatory.
		return 0;
	}

	@Override
	public void getAttendance(int id, int year) {
		//Self explanatory.
		
	}

	@Override
	public int returnBrowserId() {
		//Returns the ID of the person logged in.
		return 0;
	}

	@Override
	public void saveAndQuit() {
		//Commit changes and quit.
		
	}

	@Override
	public boolean timerStarted() {
		//See if the button was pressed to start timer.
		return false;
	}

	@Override
	public String currentDate() {
		//Self explanatory.
		return null;
	}

	@Override
	public boolean isSetup() {
		//Checks if other employees are present in the database.
		//If there isnt any, no login is required.
		return false;
	}

	@Override
	public void setRank(int rank) {
		//Set clearance rank.
		
	}

	@Override
	public void requestQuit() {
		//Kindly ask the program to quit.
		
	}

	@Override
	public boolean quitRequested() {
		//returns if requestQuit() was called.
		return false;
	}

	@Override
	public void waitInput() {
		//For those "Press enter to continue" stops.
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Main m = new Main(); //Probably terrible design, But since the fields are static I can get them from here.
		FXMLLoader loader = new FXMLLoader(getClass().getResource("EMSInterface.fxml"));
		emsTableView = new TableView<>();
		loader.setController(this);
        Parent root = loader.load();
        //Commented out the login proceedure.
		//this.registerNeighbor(m.returnPrint(), m.returnGet(), m.returnSet(), m.returnLogin());
		//this.loginProceedure();
		primaryStage.setTitle("EMS");
        thisScene = new Scene(root,600,400);
        primaryStage.setScene(thisScene);
		primaryStage.show();
		this.showEmpTable();
		//Second browser problem fixed.

	}
	
}

