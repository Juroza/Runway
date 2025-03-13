package group50.controller;

import group50.graphics.RunwayRenderer;
import group50.model.Runway;
import group50.utils.CAAParametersLoader;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.IOException;
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
    private TextField lengthInput;
    @FXML
    private TextField clearwayInput;
    @FXML
    private TextField stopwayInput;
    @FXML
    private TextField displacedThresholdInput;

    //for user role
    @FXML private Label welcomeLabel;

    // For panning
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double initialTranslateX;
    private double initialTranslateY;
    private double initialTranslateXStore;
    private double initialTranslateYStore;
    private double initialScaleX;
    private double initialScaleY;
    private boolean switchingViews=false;

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
    @FXML
    public void handleALSOverlayShow(){
        handleOverlayToggle("als",showALSToggle);
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
    private void allowOnlyDigits(TextField textField) {
        textField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
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
        handleViewTypeSelection();
    }
    @FXML
    public void handleViewTypeSelection() {
        String type = viewTypeSelector.getSelectionModel().getSelectedItem();
        resetCameraAndZoom();
        resetCameraPosition();
        if (type.equals("Top Down")) {
            resetCameraPosition();
            loadTopDownView();
            resetControlPanel();
        } else if (type.equals("Side on")) {
            resetCameraPosition();
            loadSideOnView();
            resetControlPanel();
        }
    }
    public void updateView() {
        String type = viewTypeSelector.getSelectionModel().getSelectedItem();
        resetCameraAndZoom();
        resetCameraPosition();
        if (type.equals("Top Down")) {
            loadTopDownView();
            resetControlPanel();
        } else if (type.equals("Side on")) {
            loadSideOnView();
            resetControlPanel();
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


    private void resetControlPanel(){
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
        allowOnlyDigits(lengthInput);
        allowOnlyDigits(clearwayInput);
        allowOnlyDigits(stopwayInput);
        allowOnlyDigits(displacedThresholdInput);
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
    public void resetCameraAndZoom() {
        runwayGroup.setTranslateX(0);
        runwayGroup.setTranslateY(0);
        runwayGroup.setScaleX(1);
        runwayGroup.setScaleY(1);
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


    private Runway getSelectedRunway() {
        return runwaySelector.getValue();
    }


    @FXML
    private void handleApplyParameters() {
        System.out.println("Apply button clicked");
        lengthInput.setText(lengthInput.getText().trim().replaceAll("\\s+", ""));
        clearwayInput.setText(clearwayInput.getText().trim().replaceAll("\\s+", ""));
        stopwayInput.setText(stopwayInput.getText().trim().replaceAll("\\s+", ""));
        displacedThresholdInput.setText(displacedThresholdInput.getText().trim().replaceAll("\\s+", ""));
        try{
            if (lengthInput.getText().isEmpty() ||
                clearwayInput.getText().isEmpty() ||
                stopwayInput.getText().isEmpty() ||
                displacedThresholdInput.getText().isEmpty()) {

                Alert alert = new Alert (Alert.AlertType.WARNING);
                alert.setTitle("Invalid Parameters");
                alert.setHeaderText(null);
                alert.setContentText("Please enter all runway parameters");
                alert.showAndWait();
                return;
            }
            if( Integer.parseInt(lengthInput.getText())<800){
                Alert alert = new Alert (Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Runway Length too short for operation");
                alert.showAndWait();
                return;
            }
            Runway selectedRunway = getSelectedRunway();

            if (selectedRunway != null) {
                //get old values
                int oldTORA = selectedRunway.getTORA();
                int oldTODA = selectedRunway.getTODA();
                int oldASDA = selectedRunway.getASDA();
                int oldLDA = selectedRunway.getLDA();

                //get new parameters
                int length = Integer.parseInt(lengthInput.getText());
                int clearwayLength = Integer.parseInt(clearwayInput.getText());
                int stopway = Integer.parseInt(stopwayInput.getText());
                int displacedThreshold = Integer.parseInt(displacedThresholdInput.getText());

                //get new parameter and recalculate
                selectedRunway.applyManualParameters(selectedRunway, length, clearwayLength, stopway, displacedThreshold);

                //get new values
                int newTORA = selectedRunway.getTORA();
                int newTODA = selectedRunway.getTODA();
                int newASDA = selectedRunway.getASDA();
                int newLDA = selectedRunway.getLDA();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ComparisonView.fxml"));
                Parent root = loader.load();

                ComparisonController controller = loader.getController();
                controller.setData(oldTORA, newTORA, oldTODA, newTODA, oldASDA, newASDA, oldLDA, newLDA,
                                   length, clearwayLength, stopway, displacedThreshold );

                Stage stage = new Stage();
                stage.setTitle("Runway Parameters Comparison");
                stage.setScene(new Scene(root));
                stage.show();
                updateView();
            } else {
                System.out.println("No runway selected. ");
            }
        } catch (NumberFormatException e) {

            Alert alert = new Alert (Alert.AlertType.WARNING);
            alert.setTitle("Invalid Value");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid number");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert (Alert.AlertType.WARNING);
            alert.setTitle("Fail");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load comparison view ");
            alert.showAndWait();
        }
    }

    public void setUserRole (String role) {
        welcomeLabel.setText("Welcome, " + role.toUpperCase());

        boolean isViewer = role.equalsIgnoreCase("viewer");

        lengthInput.setDisable(isViewer);
        clearwayInput.setDisable(isViewer);
        stopwayInput.setDisable(isViewer);
        displacedThresholdInput.setDisable(isViewer);

    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

