package ems.FXEms;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import ems.Browser;
import ems.EMSDataHandler;
import ems.Launcher;
import ems.LocalDataHandler;
import ems.LocalLogin;
import ems.LogFiler;
import ems.SqlDataHandler;
import ems.Login;
import ems.Printer;
import ems.SqlLogin;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FXBLauncher extends Application implements Launcher {
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button SetRankButton;

	@FXML
	private DatePicker attendanceDatePicker;

	@FXML
	private BarChart workTimeChart;

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

	EMSDataHandler dataHandler;
	Login logger;
	Printer printer;
	FXBasedBrowser browser;




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

	@FXML
	void initialize() {


	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		if(getParameters().getRaw().get(0) == "--sql") {
			dataHandler = new SqlDataHandler();
			logger = new SqlLogin();
		} else if(getParameters().getRaw().get(0) == "--local") {
			dataHandler = new LocalDataHandler();
			logger = new LocalLogin();
		}
		printer = new FXPrinter();
		browser = new FXBasedBrowser();
		dataHandler.getDependencies(this);
		logger.getDependencies(this);
		browser.getDependencies(this);
		dataHandler.initiate();
		logger.initiate();
		browser.initiate();
		browser.loginProceedure();
		browser.currentId = logger.currentId();
		FXMLLoader loader = new FXMLLoader();
		switch(dataHandler.getRank(browser.currentId)) {
		case 1:
			loader = new FXMLLoader(getClass().getResource("LowClearanceInterface.fxml"));
			break;
		case 2:
			loader = new FXMLLoader(getClass().getResource("MediumClearanceInterface.fxml"));
			break;
		case 3:
			loader = new FXMLLoader(getClass().getResource("MediumClearanceInterface.fxml"));
			break;
		case 4:
			loader = new FXMLLoader(getClass().getResource("MediumClearanceInterface.fxml"));
			break;
		case 5:
			loader = new FXMLLoader(getClass().getResource("EMSInterface.fxml"));
			break;
		default:
			System.exit(-3);
		}

		emsTableView = new TableView<>();
		loader.setController(browser);
		Parent root = loader.load();
		primaryStage.setTitle("EMS");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		browser.showEmpTable();


		//Second browser problem fixed.

	}

	@Override
	public EMSDataHandler returnHandler() {
		return dataHandler;
	}

	@Override
	public Login returnLogin() {
		return logger;
	}

	@Override
	public Printer returnPrint() {
		return printer;
	}

	@Override
	public Browser returnBrowser() {
		return browser;
	}

	@Override
	public LogFiler returnLogFiler() {
		// TODO Auto-generated method stub
		return null;
	}


}
