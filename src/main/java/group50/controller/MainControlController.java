package group50.controller;

import group50.graphics.RunwayRenderer;
import group50.model.Runway;
import group50.utils.CAAParametersLoader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
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
    @FXML private ComboBox<Runway> runwaySelector;
    @FXML private ComboBox<String> viewTypeSelector;
    @FXML private RadioButton showCAGToggle;
    @FXML private RadioButton showClearwayToggle;
    List<Runway> runwayList;

    @FXML
    private Pane viewContainer;
    @FXML
    private RadioButton showToraToggle;
    @FXML
    private RadioButton showTodaToggle;
    @FXML
    private RadioButton showResaToggle;
    @FXML
    private RadioButton showLdaToggle;
    @FXML
    private RadioButton showAsdaToggle;

    @FXML
    private TextField toraInput;
    @FXML
    private TextField todaInput;
    @FXML
    private TextField asdaInput;
    @FXML
    private TextField ldaInput;


    // For panning
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double initialTranslateX;
    private double initialTranslateY;
    private double initialTranslateXStore;
    private double initialTranslateYStore;
    private double initialScaleX;
    private double initialScaleY;

    List<Node> objs= new ArrayList<>();

    // The group that will hold rectangles (grass + runway)
    private Group runwayGroup = new Group();
    private Group runwayGroupStore = new Group();
    @FXML
    public void handleToraOverlayShow() {
        handleOverlayToggle("tora", showToraToggle);
    }

    @FXML
    public void handleTodaOverlayShow() {
        handleOverlayToggle("toda", showTodaToggle);
    }

    @FXML
    public void handleResaOverlayShow() {
        handleOverlayToggle("resa", showResaToggle);
    }

    @FXML
    public void handleLdaOverlayShow() {
        handleOverlayToggle("lda", showLdaToggle);
    }

    @FXML
    public void handleAsdaOverlayShow() {
        handleOverlayToggle("asda", showAsdaToggle);
    }
    private void handleOverlayToggle(String id, ToggleButton toggleButton) {
        if (!toggleButton.isSelected()) {
            // Remove the overlay
            Node overlayNode = runwayGroup.lookup("#" + id);
            if (overlayNode != null) {
                if (runwayGroupStore.lookup("#" + id) == null) {
                    runwayGroupStore.getChildren().add(overlayNode);
                }
                runwayGroup.getChildren().remove(overlayNode);
            } else {
                System.out.println("Node not found for removal: " + id);
            }
        } else {
            // Add the overlay back
            Node overlayNode = runwayGroupStore.lookup("#" + id);
            if (overlayNode != null) {
                overlayNode.setId(id); // Ensure ID is set before lookup
                runwayGroup.getChildren().add(overlayNode);
            } else {
                System.out.println("Node not found for re-adding: " + id);
            }
        }
    }
    public void storeInitialCameraPosition() {
        initialTranslateXStore = runwayGroup.getTranslateX();
        initialTranslateYStore = runwayGroup.getTranslateY();
        initialScaleX = runwayGroup.getScaleX();
        initialScaleY = runwayGroup.getScaleY();
    }
    public void resetCameraPosition() {
        runwayGroup.setTranslateX(initialTranslateXStore);
        runwayGroup.setTranslateY(initialTranslateYStore);
        runwayGroup.setScaleX(initialScaleX);
        runwayGroup.setScaleY(initialScaleY);
    }


    @FXML
    public void handleRunwaySelectorInput(){
        System.out.println("aoifnoaefinwopfinaow 11");
    }
    @FXML
    public void handleViewTypeSelection() {
        String type = viewTypeSelector.getSelectionModel().getSelectedItem();
        if (type.equals("Top Down")) {
            resetCameraPosition();
            loadTopDownView();
            reestControlPanel();
        } else if (type.equals("Side on")) {
            resetCameraPosition();
            loadSideOnView();
            reestControlPanel();
        }
    }
    @FXML
    public void handleClearwayOverlayShow(){
        handleOverlayToggle("clearway",showClearwayToggle);
    }
    @FXML
    public void handleCAGOverlayShow(){
        handleOverlayToggle("CAG",showCAGToggle);
    }


    private void reestControlPanel(){
        showToraToggle.setSelected(false);
        showResaToggle.setSelected(false);
        showTodaToggle.setSelected(false);
        showLdaToggle.setSelected(false);
        showAsdaToggle.setSelected(false);
        overlayRunThrough();

    }
    private void overlayRunThrough(){
        handleAsdaOverlayShow();
        handleLdaOverlayShow();
        handleResaOverlayShow();
        handleTodaOverlayShow();
        handleToraOverlayShow();
        handleCAGOverlayShow();
        handleClearwayOverlayShow();
    }





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        runwayList= new ArrayList<Runway>();
        Runway run1= new Runway("runner",3000,3000,60,500,600,0,60);
        run1.setClearedAndGradedWidth(3500);
        run1.setClearedAndGradedLengthBeyondRunwayEnds(600);
        runwayList.add(run1);
        viewTypeSelector.getSelectionModel().select(0);

        viewContainer.getChildren().add(runwayGroup);

        loadTopDownView();
        overlayRunThrough();

        runwaySelector.setItems(FXCollections.observableArrayList(runwayList));
        if (!runwayList.isEmpty()) {
            runwaySelector.getSelectionModel().select(0);
        }



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
        viewContainer.setOnScroll(event -> {
            runwayGroup.setTranslateX(runwayGroup.getTranslateX() + event.getDeltaX());
            runwayGroup.setTranslateY(runwayGroup.getTranslateY() + event.getDeltaY());
        });



    }


    private void loadTopDownView() {
        // Clear any existing shapes from the group
        runwayGroup.getChildren().clear();

       objs= RunwayRenderer.generateTopDownRunway(runwayList.get(0));
        runwayGroup.getChildren().addAll(objs);
        runwayGroupStore.getChildren().addAll(RunwayRenderer.generateTopDownRunway(runwayList.get(0)));


        runwayGroup.setTranslateX(0);
        runwayGroup.setTranslateY(0);
        double windowWidth = 800;
        double windowHeight = 600;
        Ellipse grassArea = (Ellipse) objs.get(0);
        runwayGroup.setTranslateX(-grassArea.getCenterX() + (windowWidth/ 2));
        runwayGroup.setTranslateY(-grassArea.getCenterY() + windowHeight/ 2);
        storeInitialCameraPosition();
    }
    private void loadSideOnView() {
        // Clear any existing shapes from the group
        runwayGroup.getChildren().clear();
        objs= RunwayRenderer.generateSideOnRunway(runwayList.get(0));
        runwayGroup.getChildren().addAll(objs);
        runwayGroupStore.getChildren().addAll(RunwayRenderer.generateTopDownRunway(runwayList.get(0)));


        runwayGroup.setTranslateX(0);
        runwayGroup.setTranslateY(0);
        double windowWidth = 800;
        double windowHeight = 600;

// Get the runway instead of grass
        Rectangle runwayRect = (Rectangle) runwayGroup.lookup("#runway");

// Compute the runway’s center in its parent
        double runwayCenterX = (runwayRect.getWidth() / 2);
        double runwayCenterY = (runwayRect.getHeight() / 2);

// Translate so the camera centers on the runway
        runwayGroup.setTranslateX(-runwayCenterX + (windowWidth / 2));
        runwayGroup.setTranslateY(-runwayCenterY + (windowHeight / 2));

    }


    @FXML
    private void handleApplyParameters() {
        try {
            int tora = Integer.parseInt(toraInput.getText());
            int toda = Integer.parseInt(todaInput.getText());
            int asda = Integer.parseInt(asdaInput.getText());
            int lda = Integer.parseInt(ldaInput.getText());

            Runway selectedRunway = getSelectedRunway();
            if (selectedRunway != null) {
                CAAParametersLoader.applyManualParameters(selectedRunway, tora, toda, asda, lda);
                System.out.println("Parameters applied to " + selectedRunway.getName());
            } else {
                System.out.println("No runway selected.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter valid numbers.");
        }
    }

    private Runway getSelectedRunway() {
        return runwaySelector.getValue();
    }
}

