package group50;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import group50.network.Firebase;
import group50.utils.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Map;

import group50.controller.LoginController;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
public class App  extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        Firebase.initialize();
        if(!Firebase.isInternetAvailable()){
            Alert alert = new Alert (Alert.AlertType.WARNING);
            alert.setTitle("Not Connected to Internet");
            alert.setHeaderText(null);
            alert.setContentText("Internet Connection required");
            alert.showAndWait();
            System.exit(1);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setStage(stage);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("styles.css");

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
