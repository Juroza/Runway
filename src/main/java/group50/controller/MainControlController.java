package group50.controller;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
public class MainControlController  implements Initializable  {
    @FXML private Pane runwayView;
    @FXML private ComboBox<String> runwaySelector;

    @FXML
    private Pane viewContainer;

    // For panning
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double initialTranslateX;
    private double initialTranslateY;

    // The group that will hold rectangles (grass + runway)
    private Group runwayGroup = new Group();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        viewContainer.getChildren().add(runwayGroup);

        loadTopDownView();


        viewContainer.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            initialTranslateX = runwayGroup.getTranslateX();
            initialTranslateY = runwayGroup.getTranslateY();
        });

        viewContainer.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseAnchorX;
            double deltaY = event.getSceneY() - mouseAnchorY;
            runwayGroup.setTranslateX(initialTranslateX + deltaX);
            runwayGroup.setTranslateY(initialTranslateY + deltaY);
        });
        runwayGroup.setOnZoom(event -> {
            // The pinch "zoom factor" from the gesture:
            double zoomFactor = event.getZoomFactor();


            Point2D pivotInGroup = runwayGroup.sceneToLocal(event.getSceneX(), event.getSceneY());


            runwayGroup.setScaleX(runwayGroup.getScaleX() * zoomFactor);
            runwayGroup.setScaleY(runwayGroup.getScaleY() * zoomFactor);


            Point2D pivotInScene = runwayGroup.localToScene(pivotInGroup);


            double dx = event.getSceneX() - pivotInScene.getX();
            double dy = event.getSceneY() - pivotInScene.getY();

            runwayGroup.setTranslateX(runwayGroup.getTranslateX() + dx);
            runwayGroup.setTranslateY(runwayGroup.getTranslateY() + dy);

            event.consume();
        });

        viewContainer.setOnScroll(event -> {
            // Choose your zoom factor
            double zoomFactor = 1.05;
            if (event.getDeltaY() < 0) {
                // Zoom out
                zoomFactor = 1 / zoomFactor;
            }


            Point2D mouseInGroup = runwayGroup.sceneToLocal(event.getSceneX(), event.getSceneY());


            runwayGroup.setScaleX(runwayGroup.getScaleX() * zoomFactor);
            runwayGroup.setScaleY(runwayGroup.getScaleY() * zoomFactor);


            Point2D newMouseInScene = runwayGroup.localToScene(mouseInGroup);


            double deltaX = event.getSceneX() - newMouseInScene.getX();
            double deltaY = event.getSceneY() - newMouseInScene.getY();

            // 5) Update the group's translation to compensate
            runwayGroup.setTranslateX(runwayGroup.getTranslateX() + deltaX);
            runwayGroup.setTranslateY(runwayGroup.getTranslateY() + deltaY);
        });


    }


    private void loadTopDownView() {
        // Clear any existing shapes from the group
        runwayGroup.getChildren().clear();
        Image grassImage = new Image(
                getClass().getResource("/images/gg.jpeg").toExternalForm()
        );
        Image runwayImage = new Image(
                getClass().getResource("/images/158.jpg").toExternalForm()
        );

        ImagePattern grassPattern = new ImagePattern(
                grassImage,
                0, 0,                         // Pattern anchor at top-left
                grassImage.getWidth(),        // The repeating tile width
                grassImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        ImagePattern runwayPattern = new ImagePattern(
                runwayImage,
                0, 0,                         // Pattern anchor at top-left
                runwayImage.getWidth(),        // The repeating tile width
                runwayImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        // Grass
        Rectangle grassRect = new Rectangle(10000, 10000, Color.LIGHTGREEN);
        grassRect.setFill(grassPattern);


        Rectangle runwayRect = new Rectangle(7830, 600, Color.GRAY);
        runwayRect.setFill(runwayPattern);

        runwayRect.setLayoutX(grassRect.getWidth() / 2 - runwayRect.getWidth() / 2);
        runwayRect.setLayoutY(grassRect.getHeight() / 2 - runwayRect.getHeight() / 2);



        runwayGroup.getChildren().addAll(grassRect, runwayRect);


        runwayGroup.setTranslateX(0);
        runwayGroup.setTranslateY(0);
        double windowWidth = 800;
        double windowHeight = 600;


        runwayGroup.setTranslateX(-(grassRect.getWidth() / 2) + windowWidth / 2);
        runwayGroup.setTranslateY(-(grassRect.getHeight() / 2) + windowHeight / 2);
    }


}