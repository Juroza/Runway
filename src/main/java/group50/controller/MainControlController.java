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
import javafx.scene.image.ImageView;
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
import java.util.Objects;
import java.util.ResourceBundle;
public class MainControlController  implements Initializable  {
    @FXML private Pane runwayView;
    @FXML private ComboBox<Runway> runwaySelector;
    @FXML private ComboBox<String> viewTypeSelector;
    @FXML private RadioButton showCAGToggle;
    @FXML private RadioButton showClearwayToggle;
    @FXML
    private ImageView arrowImage;
    private static final double MIN_SCALE = 0.0000001;
    private static final double MAX_SCALE = 50000.0;

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
    private RadioButton showALSToggle;


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
        System.out.println("joaeirjeoirf aoiefnoeinf");
        handleOverlayToggle("lda", showLdaToggle);
    }

    @FXML
    public void handleAsdaOverlayShow() {
        handleOverlayToggle("asda", showAsdaToggle);
    }
    @FXML
    public void handleALSOverlayShow(){
        System.out.println("OAFNOAEIPFNOEI");
        handleOverlayToggle("als",showALSToggle);
    }

    private void handleOverlayToggle(String id, ToggleButton toggleButton) {
        if (!toggleButton.isSelected()) {
            // Remove the overlay
            Node overlayNode = runwayGroup.lookup("#" + id);
            if (overlayNode != null) {
                System.out.println("Is there");
                if (runwayGroupStore.lookup("#" + id) == null) {
                    System.out.println("Is there in store");
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
        showCAGToggle.setSelected(false);
        showClearwayToggle.setSelected(false);
        showALSToggle.setSelected(false);
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
        handleALSOverlayShow();
    }





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        arrowImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/arrow.png"))));
        arrowImage.setScaleX(2);
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
            double zoomFactor = event.getZoomFactor();
            Point2D pivotInGroup = runwayGroup.sceneToLocal(event.getSceneX(), event.getSceneY());

            double newScaleX = runwayGroup.getScaleX() * zoomFactor;
            double newScaleY = runwayGroup.getScaleY() * zoomFactor;

            // Clamp to prevent negative or zero scale
            // Clamp between min and max
            newScaleX = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScaleX));
            newScaleY = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScaleY));

            runwayGroup.setScaleX(Math.max(MIN_SCALE, newScaleX));
            runwayGroup.setScaleY(Math.max(MIN_SCALE, newScaleY));

            Point2D pivotInScene = runwayGroup.localToScene(pivotInGroup);

            double dx = event.getSceneX() - pivotInScene.getX();
            double dy = event.getSceneY() - pivotInScene.getY();

            runwayGroup.setTranslateX(runwayGroup.getTranslateX() + dx);
            runwayGroup.setTranslateY(runwayGroup.getTranslateY() + dy);

            event.consume();
        });


        viewContainer.setOnScroll(event -> {
            if (event.isControlDown()) {
                double zoomFactor = event.getDeltaY() > 0 ? 1.05 : 0.95;
                Point2D mouseInGroup = runwayGroup.sceneToLocal(event.getSceneX(), event.getSceneY());

                double newScaleX = runwayGroup.getScaleX() * zoomFactor;
                double newScaleY = runwayGroup.getScaleY() * zoomFactor;
                // Clamp between min and max
                newScaleX = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScaleX));
                newScaleY = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScaleY));


                // Clamp to prevent negative or zero scale
                runwayGroup.setScaleX(Math.max(MIN_SCALE, newScaleX));
                runwayGroup.setScaleY(Math.max(MIN_SCALE, newScaleY));

                Point2D newMouseInScene = runwayGroup.localToScene(mouseInGroup);

                double deltaX = event.getSceneX() - newMouseInScene.getX();
                double deltaY = event.getSceneY() - newMouseInScene.getY();

                runwayGroup.setTranslateX(runwayGroup.getTranslateX() + deltaX);
                runwayGroup.setTranslateY(runwayGroup.getTranslateY() + deltaY);

            } else {
                // Panning with scroll
                runwayGroup.setTranslateX(runwayGroup.getTranslateX() + event.getDeltaX());
                runwayGroup.setTranslateY(runwayGroup.getTranslateY() + event.getDeltaY());
            }
            event.consume();
        });






    }


    private void loadTopDownView() {
        showToraToggle.setDisable(false);
        showCAGToggle.setDisable(false);
        showAsdaToggle.setDisable(false);
        showTodaToggle.setDisable(false);
        showClearwayToggle.setDisable(false);
        showALSToggle.setDisable(true);
        showResaToggle.setDisable(true);
        // Clear any existing shapes from the group
        runwayGroup.getChildren().clear();
        runwayGroupStore.getChildren().clear();
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
        showToraToggle.setDisable(true);
        showCAGToggle.setDisable(true);
        showAsdaToggle.setDisable(true);
        showTodaToggle.setDisable(true);
        showClearwayToggle.setDisable(true);
        showALSToggle.setDisable(false);
        showResaToggle.setDisable(false);


        // Clear any existing shapes from the group
        runwayGroup.getChildren().clear();
        runwayGroupStore.getChildren().clear();
        objs= RunwayRenderer.generateSideOnRunway(runwayList.get(0));
        runwayGroup.getChildren().addAll(objs);
        runwayGroupStore.getChildren().addAll(RunwayRenderer.generateSideOnRunway(runwayList.get(0)));

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

