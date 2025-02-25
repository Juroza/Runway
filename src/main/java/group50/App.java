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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HelloView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 200);
        stage.setScene(scene);
        stage.setTitle("Real Information");
        logger.info("beerus");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
