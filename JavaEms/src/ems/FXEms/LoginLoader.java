package ems.FXEms;
	
import java.util.ResourceBundle;

import ems.Browser;
import ems.Launcher;
import ems.LogFiler;

import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.PasswordField;
import ems.TextLogFiler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class LoginLoader extends Application {
	
	
	int logId = -1;
	

    
    private Browser browse;
    
    private Stage thisStage;
    
    private LogFiler logFiler;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField UsernameField;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private Text UsernameTitle;

    @FXML
    private Text PasswordTitle;

    @FXML
    private Text LoginTitle;

    @FXML Button SubmitButton;
    
    

	@FXML
	void LoginRequest(ActionEvent event) {
		int id = -1;
		try {
		id = Integer.parseInt(UsernameField.getText());
		} catch (NumberFormatException e) {
			if (id == -1) {
				return;
			}
			Parent root;
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginResultWindow.fxml"));
			try {
				root = fxmlLoader.load();
				LoginResultController controller = (LoginResultController) fxmlLoader.getController();
				controller.setIfLoginSuccess(false,"Invalid ID. Please check your input and try again.");
				Stage stage = new Stage();
				stage.setTitle("Logging in....");
				stage.setScene(new Scene(root,600,400));
				stage.show();
			} catch (IOException e1) {
				logFiler.saveStackTrace(e1);
				browse.print("Something went wrong. Please check the log file.");
			}
			
		}
		char[] password = PasswordField.getText().toCharArray();
		if(id == -1 || (password.length == 0)) {
			return;
		}
		if(browse.login(id, password)) {
			logId = id;
			thisStage = (Stage) SubmitButton.getScene().getWindow();
		    thisStage.close();


		} else {
			Parent root;
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginResultWindow.fxml"));
				root = fxmlLoader.load();
				LoginResultController controller = (LoginResultController) fxmlLoader.getController();
				controller.setIfLoginSuccess(false,"ID and password does not match. Please check and try again.");
				Stage stage = new Stage();
				stage.setTitle("Logging in....");
				stage.setScene(new Scene(root,600,400));
				stage.showAndWait();
			} catch (IOException e) {
				
			}
		}
		



	}

    @FXML
    void initialize() {
    }
    
    
    public void getDependenciesFromBrowser(FXBasedBrowser b) {
    	//This method only exists for this login in particular, due to it being linked to FXBasedBrowser. Thus, it does not need to take
    	//Compatibility into account.
    	logFiler = b.logFiler;
    	browse = b;
    }
    
    
	public int returnLoggedId() {
		return logId;
	}
	
	@Override
	 public void start(Stage primaryStage) {
		try {
			Platform.setImplicitExit(false);
			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			
			
			Scene scene = new Scene(root,600,400);
			primaryStage.sizeToScene();
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			PasswordTitle.wrappingWidthProperty().bind(primaryStage.widthProperty());
			LoginTitle.wrappingWidthProperty().bind(primaryStage.widthProperty());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
