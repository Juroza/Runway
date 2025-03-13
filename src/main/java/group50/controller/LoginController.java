package group50.controller;

import group50.utils.DatabaseManager;
import group50.controller.MainControlController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private ComboBox<String> airportComboBox;

    private Stage stage;


    @FXML
    public void initialize() {
        airportComboBox.getItems().addAll("LHR", "LGW", "JFK");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (DatabaseManager.validateUser(username, password)) {
            String role = DatabaseManager.getUserRole(username);
            loadMainContent(role);
        } else {
            errorLabel.setText("Incorrect user name or password！");
        }
    }

    private void loadMainContent(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainControlView.fxml"));
            Parent root = loader.load();

            MainControlController controller = loader.getController();
            controller.setUserRole(role);

            Scene scene = new Scene(root, 1980, 1080);
            stage.setScene(scene);
            stage.setTitle("Runway");
            scene.getStylesheets().add("styles.css");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
