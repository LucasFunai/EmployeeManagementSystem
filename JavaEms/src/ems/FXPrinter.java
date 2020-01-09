package ems;

import java.io.IOException;

import javafx.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FXPrinter implements Printer {
	

    @FXML
    private Text descriptionText;

    @FXML
    private TextField inputArea;

    @FXML
    private Button okButton;
	
	
	FXPrinter(){
		
	}

	@Override
	public void printInfo(String info) {
		Stage primaryStage = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("FXPrint.fxml"));
			Scene scene = new Scene(root, 400, 400);
	        primaryStage.setScene(scene);
	        descriptionText.setText(info);
	        primaryStage.show();
		} catch (IOException e) {
			
		}
		
		
	}
	
	public String printAndGetInput(String info) {
		Stage primaryStage = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("FXPrint.fxml"));
			Scene scene = new Scene(root, 400, 400);
	        primaryStage.setScene(scene);
	        descriptionText.setText(info);
	        primaryStage.show();
	        while(!okButton.isArmed()) {
	        	
	        }
	        return inputArea.getText();
		} catch (IOException e) {
			return null;
		}
		
		
	}
	
	
	

}
