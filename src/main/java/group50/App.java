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

public class App  extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainControlView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1980, 1080);
        stage.setScene(scene);
        stage.setTitle("Runway");
        scene.getStylesheets().add("styles.css");
        logger.info("beerus");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
