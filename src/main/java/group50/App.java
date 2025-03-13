package group50;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import group50.controller.LoginController;

public class App  extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
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
