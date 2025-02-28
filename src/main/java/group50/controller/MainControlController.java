package group50.controller;

import group50.graphics.RunwayRenderer;
import group50.model.Runway;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
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
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
public class MainControlController  implements Initializable  {
    @FXML private Pane runwayView;
    @FXML private ComboBox<String> runwaySelector;
    List<Runway> runwayList;

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
        runwayList= new ArrayList<Runway>();
        Runway run1= new Runway("runner",3000,3000,60,50,0,60);
        runwayList.add(run1);

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
        List<Node> objs= RunwayRenderer.generateTopDownRunway(runwayList.get(0));
        runwayGroup.getChildren().addAll(objs);


        runwayGroup.setTranslateX(0);
        runwayGroup.setTranslateY(0);
        double windowWidth = 800;
        double windowHeight = 600;
        Ellipse grassArea = (Ellipse) objs.get(0);
        runwayGroup.setTranslateX(-grassArea.getCenterX() + (windowWidth/ 2));
        runwayGroup.setTranslateY(-grassArea.getCenterY() + windowHeight/ 2);
    }


}

