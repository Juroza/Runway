package group50.controller;


import group50.graphics.RunwayRenderer;
import group50.model.Obstacle;
import group50.model.Runway;

import java.awt.*;
import java.io.*;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import group50.utils.DatabaseManager;
import group50.utils.ObstacleManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;
import javafx.scene.shape.Rectangle;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class MainControlController  implements Initializable  {
    @FXML private Pane runwayView;
    @FXML private ComboBox<Runway> runwaySelector;
    @FXML private ComboBox<String> viewTypeSelector;
    @FXML private ComboBox<Obstacle> obstacleComboBox;
    @FXML private RadioButton showCAGToggle;
    @FXML private RadioButton showClearwayToggle;
    @FXML private MenuItem displayHelpMenuItem;
    @FXML private void handelDisplayHelp(){
      showPDF();
    }


    @FXML
    private ImageView arrowImage;
    private List<StoredObstacle> storedObstacles = new ArrayList<>();

    private static class StoredObstacle {
        double localX, localY;
        String imagePath;
        double scale;

        public StoredObstacle(double x, double y, String path, double scale) {
            this.localX = x;
            this.localY = y;
            this.imagePath = path;
            this.scale = scale;
        }
    }
    private void storeObstacles() {
        storedObstacles.clear();
        for (Node node : runwayGroup.getChildren()) {
            if (node instanceof ImageView) {
                ImageView obstacle = (ImageView) node;
                String imagePath = (String) obstacle.getUserData(); // Ensure path is stored
                if (imagePath != null) {
                    Point2D localPos = new Point2D(obstacle.getX(), obstacle.getY());
                    storedObstacles.add(new StoredObstacle(localPos.getX(), localPos.getY(), imagePath, obstacle.getFitWidth()));
                }
            }
        }
    }
    private void restoreObstacles() {
        for (StoredObstacle obstacleData : storedObstacles) {
            addObstacleAt(obstacleData.localX, obstacleData.localY, obstacleData.imagePath, obstacleData.scale);
        }
    }
    private void addObstacleAt(double x, double y, String path, double scale) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(scale);
        imageView.setFitHeight((image.getHeight() / image.getWidth()) * scale);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setUserData(path);
        imageView.setRotate(-90);

        runwayGroup.getChildren().add(imageView);
        stopPlacing();
        resetCursorToDefault();

    }

    private static final double MIN_SCALE = 0.0000001;
    private static final double MAX_SCALE = 50000.0;

    List<Runway> runwayList= new ArrayList<>();

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

    //for add obstacle
    @FXML private Button addObstacleButton;
    @FXML private Label obstacleNotificationLabel;

    //for user role
    @FXML private Label welcomeLabel;
    @FXML private Label airportLabel;
    @FXML private MenuBar controlMenu;
    // For panning
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double initialTranslateX;
    private double initialTranslateY;
    private double initialScaleX;
    private double initialScaleY;
    private boolean switchingViews=false;

    List<Node> objs= new ArrayList<>();
    private String username;
    private String role;
    private String password;

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
        resetCursorToDefault();
        handleOverlayToggle("asda", showAsdaToggle);
    }
    @FXML
    public void handleALSOverlayShow(){
        initializeCursor();
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
                overlayNode.setId(id);
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

    public void resetCameraPosition() {
        centerRunwayAtDefaultScale();
    }

    private void showPDF() {
        try {
            InputStream pdfStream = getClass().getResourceAsStream("/images/Help-Document.pdf");
            if (pdfStream != null) {
                // Create a temporary file
                File tempPdf = File.createTempFile("Help-Document", ".pdf");
                tempPdf.deleteOnExit();

                // Copy the stream to the temporary file
                try (OutputStream out = new FileOutputStream(tempPdf)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = pdfStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }

                // Open the file with the default system viewer
                Desktop.getDesktop().open(tempPdf);
            } else {
                System.out.println("PDF file not found in resources!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRunwaySelectorInput(){
        handleViewTypeSelection();
    }
    @FXML
    public void handleViewTypeSelection() {
        String type = viewTypeSelector.getSelectionModel().getSelectedItem();
        if (type.equals("Top Down")) {

            loadTopDownView();
            resetControlPanel();
        } else if (type.equals("Side on")) {
            storedObstacles.clear();
            loadSideOnView();
            resetControlPanel();
        }
        resetCameraPosition();
        resetCursorToDefault();
        stopPlacing();
        if(!storedObstacles.isEmpty()){
            restoreObstacles();
        }
    }

    @FXML
    public void handleResetView() {
        resetCameraPosition();

    }
    private void addImageAtClick(double sceneX, double sceneY,String path,double scale) {

        Point2D localPoint = runwayGroup.sceneToLocal(sceneX, sceneY);


        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));


        double originalWidth = image.getWidth();
        double originalHeight = image.getHeight();


        double scaleFactor = scale / originalWidth;


        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(scale);
        imageView.setFitHeight(originalHeight * scaleFactor);

        double offsetX = imageView.getFitWidth() / 2;
        double offsetY = imageView.getFitHeight() / 2;


        imageView.setX(localPoint.getX() - offsetX);
        imageView.setY(localPoint.getY() - offsetY);
        imageView.setUserData(path);


        runwayGroup.getChildren().add(imageView);
    }





    public void updateView() {
        String type = viewTypeSelector.getSelectionModel().getSelectedItem();
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
    @FXML
    public boolean handleImportFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Airport File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );


        Stage stage = new Stage();


        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            System.out.println("File selected: " + file.getAbsolutePath());
            loadXML(file);
            return true;
        }
        return false;
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
    private void stopPlacing(){
        runwayGroup.setOnMouseClicked(mouseEvent -> {;
        });
    }



    private void initializeCursor() {
        if(viewContainer.getScene()==null) return;
        Obstacle obstacle=obstacleComboBox.getValue();

        Image cursorImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(obstacle.getPath())));


        double originalWidth = cursorImage.getWidth();
        double originalHeight = cursorImage.getHeight();


        double baseWidth = obstacle.getScale();
        double scaleFactor = baseWidth / originalWidth;


        cursorView = new ImageView(cursorImage);


        cursorView.setFitWidth(baseWidth);
        cursorView.setFitHeight(originalHeight * scaleFactor);

        cursorView.setMouseTransparent(true);


        viewContainer.getChildren().add(cursorView);


        test(obstacle.getScale());


        viewContainer.setOnMouseMoved(event -> {
            cursorView.setX(event.getX() - cursorView.getFitWidth() / 2);
            cursorView.setY(event.getY() - cursorView.getFitHeight() / 2);
        });
        runwayGroup.setOnMouseClicked(mouseEvent -> {
            addImageAtClick(mouseEvent.getSceneX(), mouseEvent.getSceneY(), obstacle.getPath(),baseWidth);

            storeObstacles();
            resetCursorToDefault();
            double distance=getObstacleDistanceFromRunway();
            Runway run=runwaySelector.getValue();
            obstacle.setDistance((int) distance);
            run.setObstacle(obstacle);
            stopPlacing();
            handleViewTypeSelection();

        });
    }
    private ImageView cursorView;
    public void resetCursorToDefault() {
        if (viewContainer != null) {
            viewContainer.getChildren().remove(cursorView);
            viewContainer.setCursor(Cursor.DEFAULT);
            runwayGroup.setCursor(Cursor.DEFAULT);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       try {
           if(!handleImportFile()){
               Alert alert = new Alert (Alert.AlertType.WARNING);
               alert.setTitle("Couldnt Load Airport File");
               alert.setHeaderText(null);
               alert.setContentText("Airport File required");
               alert.showAndWait();
               throw new RuntimeException();
           }



           arrowImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/arrow.png"))));
           arrowImage.setScaleX(2);
           allowOnlyDigits(lengthInput);
           allowOnlyDigits(clearwayInput);
           allowOnlyDigits(stopwayInput);
           allowOnlyDigits(displacedThresholdInput);
           viewTypeSelector.getSelectionModel().select(0);

           viewContainer.getChildren().add(runwayGroup);
           handleViewTypeSelection();
           overlayRunThrough();
           Platform.runLater(this::resetCameraPosition);

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
               Obstacle obstacle= obstacleComboBox.getValue();
               if(obstacle!=null){
                   cursorView.setFitWidth(obstacle.getScale() * newScaleX);
                   cursorView.setFitHeight((cursorView.getImage().getHeight() / cursorView.getImage().getWidth()) * cursorView.getFitWidth());


               }

               runwayGroup.setTranslateX(runwayGroup.getTranslateX() + dx);
               runwayGroup.setTranslateY(runwayGroup.getTranslateY() + dy);



               event.consume();
           });


           viewContainer.setOnScroll(event -> {
               if (event.isControlDown()) { // Check if pinch/zoom gesture is happening
                   double zoomFactor = event.getDeltaY() > 0 ? 1.05 : 0.95;
                   Point2D mouseInGroup = runwayGroup.sceneToLocal(event.getSceneX(), event.getSceneY());

                   // **Get current scale**
                   double newScaleX = runwayGroup.getScaleX() * zoomFactor;
                   double newScaleY = runwayGroup.getScaleY() * zoomFactor;

                   // **Clamp between min and max scale**
                   newScaleX = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScaleX));
                   newScaleY = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScaleY));

                   // **Apply scale**
                   runwayGroup.setScaleX(newScaleX);
                   runwayGroup.setScaleY(newScaleY);

                   // **Ensure zoom happens at correct pivot (adjusted for rotation)**
                   Point2D newMouseInScene = runwayGroup.localToScene(mouseInGroup);
                   double deltaX = event.getSceneX() - newMouseInScene.getX();
                   double deltaY = event.getSceneY() - newMouseInScene.getY();
                   Obstacle obstacle= obstacleComboBox.getValue();
                   if(obstacle!=null){
                       cursorView.setFitWidth(obstacle.getScale() * newScaleX);
                       cursorView.setFitHeight((cursorView.getImage().getHeight() / cursorView.getImage().getWidth()) * cursorView.getFitWidth());


                   }


                   runwayGroup.setTranslateX(runwayGroup.getTranslateX() + deltaX);
                   runwayGroup.setTranslateY(runwayGroup.getTranslateY() + deltaY);


               } else { // Normal scrolling (panning)
                   runwayGroup.setTranslateX(runwayGroup.getTranslateX() + event.getDeltaX());
                   runwayGroup.setTranslateY(runwayGroup.getTranslateY() + event.getDeltaY());
                   // Scale cursor proportionally (relative to original)

                   runwayGroup.setTranslateX(runwayGroup.getTranslateX() + event.getDeltaX());
                   runwayGroup.setTranslateY(runwayGroup.getTranslateY() + event.getDeltaY());

                   runwayGroup.setTranslateX(runwayGroup.getTranslateX() + event.getDeltaX());
                   runwayGroup.setTranslateY(runwayGroup.getTranslateY() + event.getDeltaY());
               }
               event.consume();
           });

           obstacleComboBox.getItems().addAll(ObstacleManager.getObstacles());

       }catch (RuntimeException e){
           throw new RuntimeException();
       }


    }
    private void test(double scale){
        double newScaleX = runwayGroup.getScaleX();
        cursorView.setFitWidth(scale * newScaleX);
        cursorView.setFitHeight((cursorView.getImage().getHeight() / cursorView.getImage().getWidth()) * cursorView.getFitWidth());

    }



    private void loadTopDownView() {
        showToraToggle.setDisable(false);
        showCAGToggle.setDisable(false);
        showAsdaToggle.setDisable(false);
        showTodaToggle.setDisable(false);
        showClearwayToggle.setDisable(false);
        showALSToggle.setDisable(true);
        showResaToggle.setDisable(true);

        runwayGroup.getChildren().clear();
        runwayGroupStore.getChildren().clear();
        Runway selectedRunway = runwaySelector.getValue();
        objs = RunwayRenderer.generateTopDownRunway(selectedRunway);
        runwayGroup.getChildren().addAll(objs);
        runwayGroupStore.getChildren().addAll(RunwayRenderer.generateTopDownRunway(selectedRunway));


        runwayGroup.setRotate(0);

        resetCameraPosition();

        String degree=selectedRunway.getName().substring(0,2);
        System.out.println(degree);
        int val=Integer.parseInt(degree)*10;        System.out.println(val);
        runwayGroup.setRotate(val);
        arrowImage.setRotate(val);
    }

    private void centerRunwayAtDefaultScale() {
        double defaultScale = 0.01;


        runwayGroup.setScaleX(defaultScale);
        runwayGroup.setScaleY(defaultScale);


        double viewCenterX = viewContainer.getWidth() / 2;
        double viewCenterY = viewContainer.getHeight() / 2;


        Bounds bounds = runwayGroup.localToScene(runwayGroup.getBoundsInLocal());
        double runwayCenterX = (bounds.getMinX() + bounds.getMaxX()) / 2;
        double runwayCenterY = (bounds.getMinY() + bounds.getMaxY()) / 2;


        double offsetX = viewCenterX - runwayCenterX;
        double offsetY = viewCenterY - runwayCenterY;


        runwayGroup.setTranslateX(runwayGroup.getTranslateX() + offsetX);
        runwayGroup.setTranslateY(runwayGroup.getTranslateY() + offsetY);


    }

    private void loadSideOnView() {
        // Disable overlays not relevant to side view
        showToraToggle.setDisable(true);
        showCAGToggle.setDisable(true);
        showAsdaToggle.setDisable(true);
        showTodaToggle.setDisable(true);
        showClearwayToggle.setDisable(true);
        showALSToggle.setDisable(false);
        showResaToggle.setDisable(false);


        runwayGroup.getChildren().clear();
        runwayGroupStore.getChildren().clear();


        Runway selectedRunway = runwaySelector.getValue();


        objs = RunwayRenderer.generateSideOnRunway(selectedRunway);
        runwayGroup.getChildren().addAll(objs);
        runwayGroupStore.getChildren().addAll(RunwayRenderer.generateSideOnRunway(selectedRunway));


        runwayGroup.setRotate(0);

        resetCameraPosition();

        // Get scene dimensions
        double windowWidth = viewContainer.getWidth();
        double windowHeight = viewContainer.getHeight();

        Rectangle runwayRect = (Rectangle) runwayGroup.lookup("#runway");

        if (runwayRect != null) {

            Point2D runwayScenePos = runwayRect.localToScene(runwayRect.getWidth() / 2, runwayRect.getHeight() / 2);


            double offsetX = (windowWidth / 2) - runwayScenePos.getX();
            double offsetY = (windowHeight/1.5) - runwayScenePos.getY();

            runwayGroup.setTranslateX(offsetX);
            runwayGroup.setTranslateY(offsetY);

        }

        arrowImage.setRotate(0);

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
                ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(okButton);
                alert.showAndWait();
                return;
            }
            if( Integer.parseInt(lengthInput.getText())<800){
                Alert alert = new Alert (Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Runway Length too short for operation");
                ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(okButton);
                alert.showAndWait();
                return;
            }
            Runway selectedRunway = getSelectedRunway();

            if (selectedRunway != null) {

                int oldTORA = selectedRunway.getTORA();
                int oldTODA = selectedRunway.getTODA();
                int oldASDA = selectedRunway.getASDA();
                int oldLDA = selectedRunway.getLDA();


                int length = Integer.parseInt(lengthInput.getText());
                int clearwayLength = Integer.parseInt(clearwayInput.getText());
                int stopway = Integer.parseInt(stopwayInput.getText());
                int displacedThreshold = Integer.parseInt(displacedThresholdInput.getText());


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
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert (Alert.AlertType.WARNING);
            alert.setTitle("Fail");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load comparison view ");
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
            alert.showAndWait();
        }
    }

    public void setUserRole (String role, String airport) {
        welcomeLabel.setText("Welcome, " + role.toUpperCase());
        airportLabel.setText("Welcome to " + airport + "Airport!");

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

            LoginController loginController = loader.getController();
            loginController.setStage((Stage) ((Node) event.getSource()).getScene().getWindow());

            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadXML(File xmlFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Runway");

            runwayList.clear();
            for (int i = 0; i < nList.getLength(); i++ ) {
                org.w3c.dom.Node nNode = nList.item(i);
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String name = getTextContent(eElement, "Name");
                    int length = getIntContent(eElement, "Length");
                    int stripLength = getIntContent(eElement, "StripLength");
                    int stopway = getIntContent(eElement, "Stopway");
                    int clearwayLength = getIntContent(eElement, "ClearwayLength");
                    int clearwayWidth =getIntContent(eElement, "ClearwayWidth");

                    int displacedThreshold = getIntContent(eElement, "DisplacedThreshold");
                    int RESA = getIntContent(eElement, "RESA");

                    Runway runway = new Runway(name, length, stripLength, stopway, clearwayLength,
                            clearwayWidth, displacedThreshold, RESA);
                    NodeList list = doc.getElementsByTagName("ClearedAndGradedArea");
                    if (list.getLength() > 0) {
                        Element clearedArea = (Element) list.item(0);


                        String width = clearedArea.getElementsByTagName("Width").item(0).getTextContent();
                        int widthValue = Integer.parseInt(width);


                        String lengtha = clearedArea.getElementsByTagName("LengthBeyondRunway").item(0).getTextContent();
                        int lengthValue = Integer.parseInt(lengtha);

                        System.out.println("Width: " + widthValue);
                        System.out.println("Length Beyond Runway: " + lengthValue);
                        runway.setClearedAndGradedWidth(widthValue);
                        runway.setClearedAndGradedLengthBeyondRunwayEnds(lengthValue);
                    }
                   runwayList.add(runway);
                }
            }
            updateRunwaySelector();
            System.out.println("Loaded " + runwayList.size() + " runways from XML.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to safely get text content from XML elements
    private String getTextContent(Element element, String tag) {
        NodeList nodeList = element.getElementsByTagName(tag);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : "Unknown";
    }

    private ImageView getLastPlacedObstacle() {
        List<Node> children = runwayGroup.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) { // Iterate from the last added element
            if (children.get(i) instanceof ImageView) {
                return (ImageView) children.get(i);
            }
        }
        return null; // No obstacle found
    }

    private static final double STOPWAY_SCALE_FACTOR = 10.0;
    public double  getObstacleDistanceFromRunway() {

        Rectangle stopwayRect = (Rectangle) runwayGroup.lookup("#runway");
        if (stopwayRect == null) {
            System.out.println("Stopway rectangle not found!");
            return -1;
        }


        ImageView lastObstacle = getLastPlacedObstacle();
        if (lastObstacle == null) {
            System.out.println("No obstacles found!");
            return -1;
        }


        Bounds stopwayBounds = stopwayRect.getBoundsInParent();
        Bounds obstacleBounds = lastObstacle.getBoundsInParent();


        double stopwayStartX = stopwayBounds.getMinX();
        double stopwayStartY = stopwayBounds.getMinY();


        double obstacleCenterX = (obstacleBounds.getMinX() + obstacleBounds.getMaxX()) / 2;
        double obstacleCenterY = (obstacleBounds.getMinY() + obstacleBounds.getMaxY()) / 2;


        double stopwayAngle = stopwayRect.getRotate();


        double dx = obstacleCenterX - stopwayStartX;
        double dy = obstacleCenterY - stopwayStartY;


        double projectedDistance = dx * Math.cos(Math.toRadians(stopwayAngle)) +
                dy * Math.sin(Math.toRadians(stopwayAngle));


        double realDistance = projectedDistance / STOPWAY_SCALE_FACTOR;
        realDistance+=lastObstacle.getFitHeight()/STOPWAY_SCALE_FACTOR;

        System.out.println("Rendered Distance: " + projectedDistance + "px");
        System.out.println("Real Distance: " + realDistance + " actual units");

        return realDistance;
    }

    public double  getObstacleDistanceFromStopway() {

        Rectangle stopwayRect = (Rectangle) runwayGroup.lookup("#stopway");
        if (stopwayRect == null) {
            System.out.println("Stopway rectangle not found!");
            return -1;
        }


        ImageView lastObstacle = getLastPlacedObstacle();
        if (lastObstacle == null) {
            System.out.println("No obstacles found!");
            return -1;
        }


        Bounds stopwayBounds = stopwayRect.getBoundsInParent();
        Bounds obstacleBounds = lastObstacle.getBoundsInParent();


        double stopwayStartX = stopwayBounds.getMinX();
        double stopwayStartY = stopwayBounds.getMinY();


        double obstacleCenterX = (obstacleBounds.getMinX() + obstacleBounds.getMaxX()) / 2;
        double obstacleCenterY = (obstacleBounds.getMinY() + obstacleBounds.getMaxY()) / 2;


        double stopwayAngle = stopwayRect.getRotate();


        double dx = obstacleCenterX - stopwayStartX;
        double dy = obstacleCenterY - stopwayStartY;


        double projectedDistance = dx * Math.cos(Math.toRadians(stopwayAngle)) +
                dy * Math.sin(Math.toRadians(stopwayAngle));


        double realDistance = projectedDistance / STOPWAY_SCALE_FACTOR;

        System.out.println("Rendered Distance: " + projectedDistance + "px");
        System.out.println("Real Distance: " + realDistance + " actual units");

        return realDistance;
    }


    // Helper method to safely parse integers from XML
    private int getIntContent(Element element, String tag) {
        try {
            return Integer.parseInt(getTextContent(element, tag));
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format for " + tag);
            return 0; // Default to 0 if invalid
        }
    }
    private void updateRunwaySelector() {
        if (runwaySelector != null) {

            runwaySelector.setItems(FXCollections.observableArrayList(runwayList));


            if (!runwayList.isEmpty()) {
                runwaySelector.getSelectionModel().select(0);
            }
        } else {
            System.err.println("runwaySelector is null. Check if FXML is properly linked.");
        }
    }
    @FXML
    private void handleAddObstacleType(){
        Dialog<Obstacle> dialog = new Dialog<>();
        dialog.setTitle("Add New Obstacle");
        dialog.setHeaderText("Enter details for the new obstacle:");

        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(50);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");

        TextField heightField = new TextField();
        heightField.setPromptText("Enter Height (m)");

        TextField distanceField = new TextField();
        distanceField.setPromptText("Enter Distance (m)");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Height (m):"), 0, 1);
        grid.add(heightField, 1, 1);
        grid.add(new Label("Distance (m):"), 0, 2);
        grid.add(distanceField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(addButton, cancelButton);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                try {
                    String name = nameField.getText().trim();
                    int height = Integer.parseInt(heightField.getText().trim());
                    int distance = Integer.parseInt(distanceField.getText().trim());

                    if (name.isEmpty() || height <= 0 || distance < 0) {
                        showError("Invalid Input", "Please enter valid obstacle details.");
                        return null;
                    }
                    return new Obstacle(name, height, distance, "/images/car.png", 500);

                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter valid numbers for Height and Distance.");
                    return null;
                }
            }
            return null;
        });

        Optional<Obstacle> result = dialog.showAndWait();
        result.ifPresent(this::addObstacleToList);
    }


    @FXML
    private void handleAddObstacle() {
        stopPlacing();
        resetCursorToDefault();
        initializeCursor();

    }
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void addObstacleToList(Obstacle obstacle) {
        obstacleComboBox.getItems().add(obstacle);
        obstacleComboBox.setValue(obstacle);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        if(Objects.equals(DatabaseManager.getUserRole(username), "admin")){
            Menu menuItem = new Menu("Users");
            //  nuItem.setOnAction(event -> System.out.println(itemName + " clicked!"));
            MenuItem addUsers= new MenuItem("Add User");
            addUsers.setOnAction(event->{}); //TODO
            menuItem.getItems().add(addUsers);
            controlMenu.getMenus().add(menuItem);
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

