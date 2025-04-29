package group50.controller;


import group50.network.Firebase;
import group50.utils.DatabaseManager;
import group50.controller.MainControlController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

import java.io.IOException;

public class CreateNewUserController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button createUserButton;
    @FXML private  ComboBox<String> roleSelector;

    private Stage stage;


    @FXML
    public void initialize() {



    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleCreateUser(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role= roleSelector.getSelectionModel().getSelectedItem();


        if (DatabaseManager.addUser(username, password,role)) {
            stage.close();
        } else {

            errorLabel.setText("Invalid information！");
            errorLabel.setTextFill(Color.RED);
        }
    }



}
