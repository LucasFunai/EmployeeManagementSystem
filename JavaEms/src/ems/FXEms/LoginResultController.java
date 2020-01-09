package ems.FXEms;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginResultController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text loginResultText;
    
    @FXML
    private Text resultInfoText;

    @FXML
    private Button OKbutton;

    @FXML
    void OKClicked(ActionEvent event) {
    	((Stage) OKbutton.getScene().getWindow()).close();

    }
    
    void setIfLoginSuccess(boolean result,String info) {
    	if(result) {
    		loginResultText.setText("Login success! Press OK to continue.");
    		resultInfoText.setText("");
    		
    	} else {
    		loginResultText.setText("Your password or ID is incorrect. Please check and try again.");
    		resultInfoText.setText(info);
    	}
    	
    }

    @FXML
    void initialize() {
    	OKbutton.requestFocus();
    }
}
