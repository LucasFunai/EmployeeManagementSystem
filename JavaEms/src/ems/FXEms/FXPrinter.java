package ems.FXEms;

import java.io.IOException;

import ems.Printer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXPrinter extends Application implements Printer  {
	String result;
	
	
	
    @FXML
    private TextField textField1;

    @FXML
    private Button button1;
    
    @FXML
    private Label infoLabel;

    @FXML
    void onEnterPressed(ActionEvent event) {
    	
    }

    @FXML
    void onOKPressed(ActionEvent event) {

    }
    
    @FXML
    void initialize() {
        assert textField1 != null : "fx:id=\"textField1\" was not injected: check your FXML file 'PrintWindow.fxml'.";
        assert button1 != null : "fx:id=\"button1\" was not injected: check your FXML file 'PrintWindow.fxml'.";

    }


	@Override
	public void start(Stage primaryStage) throws Exception {
		
	}

	@Override
	public void printInfo(String info) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PrintWindow.fxml"));
			loader.setController(this);
			Parent root = loader.load();
			textField1.setVisible(false);
			Stage primaryStage = new Stage();
			primaryStage.setScene(new Scene(root));
			primaryStage.setTitle("Message");
			infoLabel.setText(info);
			button1.setOnAction((event) -> {result = textField1.getText();
					primaryStage.close();});
			textField1.setOnAction((event) -> {result = textField1.getText();
					primaryStage.close();});
			primaryStage.showAndWait();
		} catch (IOException e) {
			
		}
		
	}

	@Override
	public String printAndGetInput(String info) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PrintWindow.fxml"));
			loader.setController(this);
			Parent root = loader.load();
			Stage primaryStage = new Stage();
			primaryStage.setScene(new Scene(root));
			primaryStage.setTitle("Message");
			infoLabel.setText(info);
			button1.setOnAction((event) -> {result = textField1.getText();
					primaryStage.close();});
			textField1.setOnAction((event) -> {result = textField1.getText();
					primaryStage.close();});
			primaryStage.showAndWait();
			return result;
		} catch (IOException e) {
			return null;
		}
	}

}
