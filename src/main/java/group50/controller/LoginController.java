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

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
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
        loginButton.setDisable(true);
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String selectedAirport = airportComboBox.getValue();

        if (selectedAirport == null || selectedAirport.equals("Select an airport...")) {
            errorLabel.setText("Please choose an airport!");
            errorLabel.setTextFill(Color.RED);
            return;
        }

        if (DatabaseManager.validateUser(username, password)) {
            String role = DatabaseManager.getUserRole(username);
            loadMainContent(role,username, selectedAirport);
        } else {

            errorLabel.setText("Incorrect user name or password！");
            errorLabel.setTextFill(Color.RED);
        }
        loginButton.setDisable(false);
    }

    private void loadMainContent(String role,String username, String airport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainControlView.fxml"));
            Parent root = loader.load();

            MainControlController controller = loader.getController();
            controller.setUserRole(role, airport);
            controller.setUsername(username);
            controller.setRole(role);

            Scene scene = new Scene(root, 1980, 1080);
            scene.getStylesheets().add("styles.css");

            if (this.stage == null) {
                System.out.println("Error: stage is null!");
                return;
            }

            this.stage.setScene(scene);
            this.stage.setTitle("Runway");
            this.stage.show();

        }catch (RuntimeException e){

        } catch (IOException e) {
        }
    }
}
