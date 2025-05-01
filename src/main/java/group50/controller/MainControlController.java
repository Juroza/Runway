package group50.controller;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import group50.graphics.RunwayRenderer;
import group50.model.Obstacle;
import group50.model.Runway;

import java.awt.*;
import java.io.*;

import group50.utils.Notifer;
import javafx.util.Duration;
import group50.network.Firebase;
import group50.utils.PDFGenerator;
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
import java.util.concurrent.ExecutionException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static group50.network.Firebase.readDocument;


public class MainControlController  implements Initializable  {
    @FXML private Pane runwayView;
    @FXML private ComboBox<Runway> runwaySelector;
    @FXML private ComboBox<String> viewTypeSelector;
    @FXML private ComboBox<Obstacle> obstacleComboBox;
    @FXML private RadioButton showCAGToggle;
    @FXML private RadioButton showClearwayToggle;

    @FXML private void printReport(){
        PDFGenerator.printPDF(airport,runwayList,getUsername());
    }
    @FXML private void exportReport(){
        Stage stage = new Stage();
        exportRunwayWithDialog(getSelectedRunway(),stage);
    }
    @FXML private void handelDisplayHelp(){
      showPDF();
    }


    @FXML
    private ImageView arrowImage;
    int oldTORA;
    int oldTODA ;
    long time;
    int oldASDA ;
    int oldLDA;
    private String airport;


    private void restoreObstacles() {
        runwayGroup.getChildren().removeIf(node -> node instanceof ImageView);

        Runway selectedRunway = getSelectedRunway();
        if (selectedRunway != null && selectedRunway.getObstacle() != null) {
            Obstacle obstacle = selectedRunway.getObstacle();
            //addObstacleFromRunwayDistance(getSelectedRunway(),obstacle);
        }
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





    @FXML private Button addObstacleButton;



    @FXML private Label welcomeLabel;
    @FXML private Label airportLabel;
    @FXML private MenuBar controlMenu;
    // For panning
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double initialTranslateX;
    private double initialTranslateY;



    List<Node> objs= new ArrayList<>();
    private String username;
    private String role;
    private String password;

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
            Node overlayNode = runwayGroupStore.lookup("#" + id);
            if (overlayNode != null) {
                overlayNode.setId(id);
                runwayGroup.getChildren().add(overlayNode);
            } else {
                System.out.println("Node not found for re-adding: " + id);
            }
        }
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

                Desktop.getDesktop().open(tempPdf);
            } else {
                System.out.println("PDF file not found in resources");
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
        resetCameraPosition();
        resetCursorToDefault();
        stopPlacing();
        if (type.equals("Top Down")) {

            loadTopDownView();
            resetControlPanel();
        } else if (type.equals("Side on")) {
            loadSideOnView();
            resetControlPanel();
        }
        resetCameraPosition();
        resetCursorToDefault();
        stopPlacing();
    }

    @FXML
    public void handleResetView() {
        resetCameraPosition();

    }
    private ImageView addImageAtClick(double sceneX, double sceneY,String path,double scale) {

        Point2D localPoint = runwayGroup.sceneToLocal(sceneX, sceneY);
        Obstacle obstacle = obstacleComboBox.getValue();
        if (obstacle == null) {
            System.out.println("No obstacle selected.");
            return new ImageView();
        }

        Image image = viewTypeSelector.getSelectionModel().getSelectedItem().equals("Top Down")
                ? new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/topDown/"+obstacle.getPath())))
                : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/sideOn/"+obstacle.getPath())));

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
        return imageView;
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
        Obstacle obstacle = obstacleComboBox.getValue();
        if (obstacle == null) {
            return;
        }
        Image cursorImage = viewTypeSelector.getSelectionModel().getSelectedItem().equals("Top Down")
                ? new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/topDown/"+obstacle.getPath())))
                : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/sideOn/"+obstacle.getPath())))
                ;


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
            ImageView imageView=addImageAtClick(mouseEvent.getSceneX(), mouseEvent.getSceneY(), obstacle.getPath(),baseWidth);

            resetCursorToDefault();
            double distance=getObstacleDistanceFromRunway();
            distance*=10;
            Runway run=runwaySelector.getValue();

            oldTODA=run.getTODA();
            oldTORA=run.getTORA();
            oldASDA=run.getASDA();
            oldLDA=run.getLDA();
            int offset= (int) Math.max(imageView.getFitWidth(),imageView.getFitHeight());
            System.out.println("Distance old: "+distance);
            if(distance> (double) getSelectedRunway().getLength() /2){
                distance-=offset;
                System.out.println("offset: "+offset);
            }else{
                System.out.println("offset: "+offset);
                distance+=offset;
            }
            distance/=10;
            System.out.println("Distance NEW: "+distance);
            obstacle.setDistance((int) distance);
            run.setObstacle(obstacle);
            updateObstacleInFirebase(run);
            stopPlacing();
            handleViewTypeSelection();

        });
    }
    public void updateObstacleInFirebase(Runway runway) {
        System.out.println("UPDATEEDEDAEOGF NEWWW: "+runway.getObstacle().getDistance());
        if (runway == null || runway.getID() == null || runway.getObstacle() == null) {
            return;
        }

        try {
            Firestore db = Firebase.getFirestore();
            DocumentReference runwayRef = db.collection("airports")
                    .document(airport)
                    .collection("runways")
                    .document(runway.getName());

            Map<String, Object> updates = new HashMap<>();
            updates.put("Obstacle type", runway.getObstacle().getName());
            updates.put("Obstacle height", runway.getObstacle().getHeight());
            updates.put("Obstacle distance", runway.getObstacle().getDistance());
            time= System.nanoTime();
            updates.put("Last Modified",time );

            runwayRef.update(updates)
                    .addListener(() -> System.out.println(" Obstacle updated " + runway.getID()),
                            Runnable::run);

        } catch (Exception e) {
            System.err.println( e.getMessage());
        }
    }
    private ImageView cursorView;
    public void resetCursorToDefault() {
        if (viewContainer != null) {
            viewContainer.getChildren().remove(cursorView);
            viewContainer.setCursor(Cursor.DEFAULT);
            runwayGroup.setCursor(Cursor.DEFAULT);
        }
    }
    private ListenerRegistration runwayListenerRegistration;

    private void setupRunwayListener() {
        if (runwayListenerRegistration != null) {
            runwayListenerRegistration.remove();
        }

        CollectionReference runwaysRef = Firebase.getFirestore()
                .collection("airports")
                .document(airport)
                .collection("runways");

        runwayListenerRegistration = runwaysRef.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                System.err.println("Listen failed: " + error);
                return;
            }

            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                Object id = dc.getDocument().getData().get("id");
                Object timer = dc.getDocument().getData().get("Last Modified");
                if(timer==null){
                    replaceRunwayFromFirebaseData(dc.getDocument().getData());

                    Platform.runLater(() -> {
                        Notifer notifer= new Notifer();
                        try {

                            notifer.displayTray("Runway has been updated");
                        } catch (AWTException e) {
                            System.out.println("notif e");
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            System.out.println("notif e");
                            throw new RuntimeException(e);
                        }
                    });
                }
                long timeVal = (long) timer;

                switch (dc.getType()) {
                    case ADDED:
                    case MODIFIED:
                    case REMOVED:
                        System.out.println("HERE FREDDY");
                        System.out.println(timeVal);
                        replaceRunwayFromFirebaseData(dc.getDocument().getData());

                        Platform.runLater(() -> {
                            Notifer notifer= new Notifer();
                            try {

                                notifer.displayTray("Runway has been updated");
                            } catch (AWTException e) {
                                System.out.println("notif e");
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                System.out.println("notif e");
                                throw new RuntimeException(e);
                            }
                        });
                        break;
                }
            }
        });
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
           viewTypeSelector.getSelectionModel().select(0);
           handleViewTypeSelection();

           viewContainer.getChildren().add(runwayGroup);
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
               if(obstacle!=null&&cursorView!=null){
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
    private void replaceRunwayFromFirebaseData(Map<String, Object> data) {
        if (data == null) {
            System.out.println("NEVER");
            return;
        }

        try {
            String updatedId = (String) data.get("id");
            System.out.println("HERERE  NEWWW FING");

            for (int i = 0; i < runwayList.size(); i++) {
                Runway localRunway = runwayList.get(i);

                if (localRunway.getID().equals(updatedId)) {

                    int length = Integer.parseInt(data.get("length").toString());
                    int stripLength = Integer.parseInt(data.get("stripLength").toString());
                    int stopway = Integer.parseInt(data.get("stopway").toString());
                    int clearwayLength = Integer.parseInt(data.get("clearwayLength").toString());
                    int clearwayWidth = Integer.parseInt(data.get("clearwayWidth").toString());
                    int displacedThreshold = Integer.parseInt(data.get("displacedThreshold").toString());
                    int RESA = Integer.parseInt(data.get("RESA").toString());

                    Runway updatedRunway = new Runway(data.get("name").toString(), length, stripLength, stopway, clearwayLength, clearwayWidth, displacedThreshold, RESA);
                    updatedRunway.setID(updatedId);

                    if (data.containsKey("Obstacle type") && data.containsKey("Obstacle height") && data.containsKey("Obstacle distance")) {
                        Obstacle obstacle = ObstacleManager.getObstacleByName(data.get("Obstacle type").toString());
                        if (obstacle != null) {
                            obstacle.setHeight(Integer.parseInt(data.get("Obstacle height").toString()));
                            obstacle.setDistance(Integer.parseInt(data.get("Obstacle distance").toString()));
                            updatedRunway.setObstacle(obstacle);
                        }
                    }

                    updatedRunway.redeclareALL();


                    runwayList.set(i, updatedRunway);


                    Platform.runLater(() -> {
                        runwaySelector.setItems(FXCollections.observableArrayList(runwayList));
                        runwaySelector.getSelectionModel().select(updatedRunway);
                        handleViewTypeSelection();
                    });
                    return;
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if(selectedRunway==null){
            return;
        }
        if(selectedRunway.hasObstacle()){
            selectedRunway.redeclareALL();
        }
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
        if(selectedRunway.hasObstacle()){
            selectedRunway.redeclareALL();
        }

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
        Platform.runLater(this::resetCameraPosition);


    }



    private Runway getSelectedRunway() {
        return runwaySelector.getValue();
    }

    @FXML
    private void viewData(){
        handleApplyParameters();
    }

    @FXML
    private void handleApplyParameters() {
        System.out.println("Apply button clicked");
      try{
            Runway selectedRunway = getSelectedRunway();

            if (selectedRunway != null) {




                int length = selectedRunway.getLength();
                int clearwayLength = selectedRunway.getClearwayLength();
                int stopway = selectedRunway.getStopway();
                int displacedThreshold = selectedRunway.getDisplacedThreshold();


                selectedRunway.applyManualParameters(selectedRunway, length, clearwayLength, stopway, displacedThreshold);


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
        stopPlacing();
        resetCursorToDefault();
        restoreObstacles();
    }

    public void setUserRole (String role) {
        welcomeLabel.setText("Welcome, " + username.toUpperCase());
        airportLabel.setText("Welcome to " + airport + " Airport!");

        boolean isViewer = role.equalsIgnoreCase("viewer");


    }

    private boolean isAdmin(){
        return role.equalsIgnoreCase("admin");
    }
    private boolean isEditor(){
        return role.equalsIgnoreCase("editor");
    }
    private boolean isViewer(){
        return role.equalsIgnoreCase("viewer");
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
    public void exportAirportFromFirebaseToXML(String airportName,File file) {
        try {
            CollectionReference runwaysRef = Firebase.getFirestore()
                    .collection("airports")
                    .document(airportName)
                    .collection("runways");


            List<QueryDocumentSnapshot> runwayDocs = runwaysRef.get().get().getDocuments();


            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element airportElement = doc.createElement("Airport");
            airportElement.setAttribute("name", airportName);
            doc.appendChild(airportElement);

            Element runwaysElement = doc.createElement("Runways");
            airportElement.appendChild(runwaysElement);

            for (var docSnap : runwayDocs) {
                Map<String, Object> data = docSnap.getData();

                Element runwayElement = doc.createElement("Runway");
                runwaysElement.appendChild(runwayElement);

                addTextElement(doc, runwayElement, "ID", safeString(data.get("id")));
                addTextElement(doc, runwayElement, "Name", safeString(data.get("name")));
                addTextElement(doc, runwayElement, "Length", safeString(data.get("length")));
                addTextElement(doc, runwayElement, "StripLength", safeString(data.get("stripLength")));
                addTextElement(doc, runwayElement, "Stopway", safeString(data.get("stopway")));
                addTextElement(doc, runwayElement, "ClearwayLength", safeString(data.get("clearwayLength")));
                addTextElement(doc, runwayElement, "ClearwayWidth", safeString(data.get("clearwayWidth")));
                addTextElement(doc, runwayElement, "DisplacedThreshold", safeString(data.get("displacedThreshold")));
                addTextElement(doc, runwayElement, "RESA", safeString(data.get("RESA")));
                addTextElement(doc, runwayElement, "BlastProtection", safeString(data.getOrDefault("blastProtection", "500")));

                Element clearedArea = doc.createElement("ClearedAndGradedArea");
                addTextElement(doc, clearedArea, "Width", safeString(data.get("clearwayWidth")));
                addTextElement(doc, clearedArea, "LengthBeyondRunway", "150");
                runwayElement.appendChild(clearedArea);

                Element declaredDistances = doc.createElement("DeclaredDistances");
                addTextElement(doc, declaredDistances, "TORA", safeString(data.get("length")));
                addTextElement(doc, declaredDistances, "TODA", safeString(data.get("length")));
                addTextElement(doc, declaredDistances, "ASDA", safeString(data.get("length")));
                addTextElement(doc, declaredDistances, "LDA", safeString(data.get("length")));
                runwayElement.appendChild(declaredDistances);

                Element surfaceSlopes = doc.createElement("SurfaceSlopes");
                addTextElement(doc, surfaceSlopes, "ALS", "50:1");
                addTextElement(doc, surfaceSlopes, "TOCS", "50:1");
                runwayElement.appendChild(surfaceSlopes);

                if (data.containsKey("Obstacle type")) {
                    Element obstacle = doc.createElement("Obstacle");
                    addTextElement(doc, obstacle, "Name", safeString(data.get("Obstacle type")));
                    addTextElement(doc, obstacle, "Distance", safeString(data.get("Obstacle distance")));
                    addTextElement(doc, obstacle, "Height", safeString(data.get("Obstacle height")));
                    runwayElement.appendChild(obstacle);
                }
            }




            if (file != null) {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                transformer.transform(new DOMSource(doc), new StreamResult(file));

            } else {
                System.out.println("cancelled");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to export airport from Firebase.");
        }
    }

    // Helper method
    private static String safeString(Object o) {
        return (o != null) ? o.toString() : "";
    }
    public static boolean airportExists(String airportName) {
        Firestore db = Firebase.getFirestore();

        try {
            DocumentReference airportRef = db.collection("airports").document(airportName);


            ApiFuture<DocumentSnapshot> future = airportRef.get();
            DocumentSnapshot document = future.get();


            return document.exists();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error checking if airport exists.");
            return false;
        }
    }
    public void loadXML(File xmlFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            Element airportElement = (Element) doc.getElementsByTagName("Airport").item(0);
            Element IDtElement = (Element) doc.getElementsByTagName("ID").item(0);

            airport= airportElement.getAttribute("name");

            NodeList nList = doc.getElementsByTagName("Runway");


            runwayList.clear();
            for (int i = 0; i < nList.getLength(); i++ ) {
                org.w3c.dom.Node nNode = nList.item(i);
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String id = getTextContent(eElement, "ID");
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
                    Map<String, Object> runwayData = new HashMap<>();
                    runwayData.put("id",id);
                    runwayData.put("name", name);
                    runwayData.put("length", length);
                    runwayData.put("stripLength", stripLength);
                    runwayData.put("stopway", stopway);
                    runwayData.put("clearwayLength", clearwayLength);
                    runwayData.put("clearwayWidth", clearwayWidth);
                    runwayData.put("displacedThreshold", displacedThreshold);
                    runwayData.put("RESA", RESA);
                    runway.setID(id);
                    if (list.getLength() > 0) {
                        Element clearedArea = (Element) list.item(0);


                        String width = clearedArea.getElementsByTagName("Width").item(0).getTextContent();
                        int widthValue = Integer.parseInt(width);


                        String lengtha = clearedArea.getElementsByTagName("LengthBeyondRunway").item(0).getTextContent();
                        int lengthValue = Integer.parseInt(lengtha);

                        System.out.println("Width: " + runwayData);
                        System.out.println("Length Beyond Runway: " + lengthValue);
                        runway.setClearedAndGradedWidth(widthValue);
                        runway.setClearedAndGradedLengthBeyondRunwayEnds(lengthValue);
                    }
                    NodeList obstacles = doc.getElementsByTagName("Obstacle");
                    if ( obstacles.getLength() > 0) {
                        Element clearedArea = (Element)  obstacles.item(0);


                        String obsName = clearedArea.getElementsByTagName("Name").item(0).getTextContent();


                        String obstacleDistance = clearedArea.getElementsByTagName("Distance").item(0).getTextContent();
                        int obstacleDistanceValue = Integer.parseInt(obstacleDistance );
                        String obstacleHeight = clearedArea.getElementsByTagName("Height").item(0).getTextContent();
                        int obstacleHeightValue = Integer.parseInt(obstacleHeight );

                        Obstacle obstacle= ObstacleManager.getObstacleByName(obsName);
                        System.out.println("HETETEA"+obstacle.toString());

                        assert obstacle != null;
                        obstacle.setDistance(obstacleDistanceValue);
                        obstacle.setHeight(obstacleHeightValue);
                        runwayData.put("Obstacle type", obsName);
                        runwayData.put("Obstacle distance", obstacleDistanceValue);
                        runwayData.put("Obstacle height", obstacleHeightValue);
                        runway.setObstacle(obstacle);
                    }
                   runwayList.add(runway);

                }
            }
            if(!airportExists(airport)){
                uploadAirportToFirebase(airport,runwayList);
            }else{
                if(isRunwayListMatchingFirebase(airport)) {
                    updateRunwaySelector();
                }else{
                    exportAirportFromFirebaseToXML(airport,xmlFile);
                    loadXML(xmlFile);
                }
            }
            setupRunwayListener();



            System.out.println("Loaded " + runwayList.size() + " runways from XML.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRunwayListMatchingFirebase(String airportName) {
        try {
            CollectionReference runwaysRef = Firebase.getFirestore()
                    .collection("airports")
                    .document(airportName)
                    .collection("runways");

            List<QueryDocumentSnapshot> firebaseDocs = runwaysRef.get().get().getDocuments();

            for (Runway localRunway : runwayList) {
                System.out.println(localRunway.getID()+" aoiefhoaeifhn bbbbbbb");
                boolean matchFound = false;
                for (QueryDocumentSnapshot doc : firebaseDocs) {
                    Map<String, Object> data = doc.getData();
                    if (data == null) continue;

                    String firebaseID = (String) data.get("id");
                    if (firebaseID != null && firebaseID.equals(localRunway.getID())) {

                        if (compareRunwayFields(localRunway, data)) {
                            matchFound = true;
                            break;
                        }
                        System.out.println(localRunway.getID()+" TOOO "+ firebaseID);
                    }
                }
                if (!matchFound) {
                    return false;
                }
            }

            return true;

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            return false;
        }
    }


    private boolean compareRunwayFields(Runway runway, Map<String, Object> data) {
        try {
            return
                    runway.getName().equals(data.get("name")) &&
                            runway.getLength() == Integer.parseInt(data.get("length").toString()) &&
                            runway.getStripLength() == Integer.parseInt(data.get("stripLength").toString()) &&
                            runway.getStopway() == Integer.parseInt(data.get("stopway").toString()) &&
                            runway.getClearwayLength() == Integer.parseInt(data.get("clearwayLength").toString()) &&
                            runway.getClearwayWidth() == Integer.parseInt(data.get("clearwayWidth").toString()) &&
                            runway.getRESA() == Integer.parseInt(data.get("RESA").toString()) &&
                            runway.getDisplacedThreshold() == Integer.parseInt(data.get("displacedThreshold").toString());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void uploadAirportToFirebase(String airportName, List<Runway> runwayList) {
        Firestore db = Firebase.getFirestore();

        try {

            DocumentReference airportRef = db.collection("airports").document(airportName);


            Map<String, Object> airportData = new HashMap<>();
            airportData.put("name", airportName);
            airportRef.set(airportData);


            CollectionReference runwaysRef = airportRef.collection("runways");

            for (Runway runway : runwayList) {
                Map<String, Object> runwayData = new HashMap<>();
                runwayData.put("id", runway.getID());
                runwayData.put("name", runway.getName());
                runwayData.put("length", runway.getLength());
                runwayData.put("stripLength", runway.getStripLength());
                runwayData.put("stopway", runway.getStopway());
                runwayData.put("clearwayLength", runway.getClearwayLength());
                runwayData.put("clearwayWidth", runway.getClearwayWidth());
                runwayData.put("displacedThreshold", runway.getDisplacedThreshold());
                runwayData.put("RESA", runway.getRESA());
                runwayData.put("blastProtection", runway.getBlastProtection());


                runwayData.put("clearedAndGradedWidth", runway.getClearedAndGradedWidth());
                runwayData.put("clearedAndGradedLengthBeyondRunwayEnds", runway.getClearedAndGradedLengthBeyondRunwayEnds());


                runwayData.put("TORA", runway.getTORA());
                runwayData.put("TODA", runway.getTODA());
                runwayData.put("ASDA", runway.getASDA());
                runwayData.put("LDA", runway.getLDA());


                if (runway.hasObstacle() && runway.getObstacle() != null) {
                    runwayData.put("Obstacle type", runway.getObstacle().getName());
                    runwayData.put("Obstacle distance", runway.getObstacle().getDistance());
                    runwayData.put("Obstacle height", runway.getObstacle().getHeight());
                }


                runwaysRef.document(runway.getName()).set(runwayData);
            }

            System.out.println("Airport uploaded successfully to Firebase");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to upload airport to Firebase.");
        }
    }
    public void exportRunwayWithDialog(Runway runway, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Runway XML");

        // Suggest a default file name
        fileChooser.setInitialFileName(runway.getName() + ".xml");

        // Restrict to only .xml files
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );

        // Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            saveRunwayListToXML(runwayList, airport,file.getAbsolutePath());
        } else {
            System.out.println("cancelled.");
        }
    }
    public  void saveRunwayListToXML(List<Runway> runwayList, String airportName, String filePath) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();


            Document doc = docBuilder.newDocument();
            Element airportElement = doc.createElement("Airport");
            airportElement.setAttribute("name", airportName); // Use passed airport name
            doc.appendChild(airportElement);


            Element runwaysElement = doc.createElement("Runways");
            airportElement.appendChild(runwaysElement);


            for (Runway runway : runwayList) {
                Element runwayElement = doc.createElement("Runway");
                runwaysElement.appendChild(runwayElement);

                addTextElement(doc, runwayElement, "ID", runway.getID());
                addTextElement(doc, runwayElement, "Name", runway.getName());
                addTextElement(doc, runwayElement, "Length", String.valueOf(runway.getLength()));
                addTextElement(doc, runwayElement, "StripLength", String.valueOf(runway.getStripLength()));
                addTextElement(doc, runwayElement, "Stopway", String.valueOf(runway.getStopway()));
                addTextElement(doc, runwayElement, "ClearwayLength", String.valueOf(runway.getClearwayLength()));
                addTextElement(doc, runwayElement, "ClearwayWidth", String.valueOf(runway.getClearwayWidth()));
                addTextElement(doc, runwayElement, "DisplacedThreshold", String.valueOf(runway.getDisplacedThreshold()));
                addTextElement(doc, runwayElement, "RESA", String.valueOf(runway.getRESA()));
                addTextElement(doc, runwayElement, "BlastProtection", String.valueOf(runway.getBlastProtection()));


                Element clearedArea = doc.createElement("ClearedAndGradedArea");
                addTextElement(doc, clearedArea, "Width", String.valueOf(runway.getClearedAndGradedWidth()));
                addTextElement(doc, clearedArea, "LengthBeyondRunway", String.valueOf(runway.getClearedAndGradedLengthBeyondRunwayEnds()));
                runwayElement.appendChild(clearedArea);


                Element declaredDistances = doc.createElement("DeclaredDistances");
                addTextElement(doc, declaredDistances, "TORA", String.valueOf(runway.getTORA()));
                addTextElement(doc, declaredDistances, "TODA", String.valueOf(runway.getTODA()));
                addTextElement(doc, declaredDistances, "ASDA", String.valueOf(runway.getASDA()));
                addTextElement(doc, declaredDistances, "LDA", String.valueOf(runway.getLDA()));
                runwayElement.appendChild(declaredDistances);


                Element surfaceSlopes = doc.createElement("SurfaceSlopes");
                addTextElement(doc, surfaceSlopes, "ALS", "50:1");
                addTextElement(doc, surfaceSlopes, "TOCS", "50:1");
                runwayElement.appendChild(surfaceSlopes);

                if (runway.hasObstacle() && runway.getObstacle() != null) {
                    Element obstacle = doc.createElement("Obstacle");
                    addTextElement(doc, obstacle, "Name", runway.getObstacle().getName());
                    addTextElement(doc, obstacle, "Distance", String.valueOf(runway.getObstacle().getDistance()));
                    addTextElement(doc, obstacle, "Height", String.valueOf(runway.getObstacle().getHeight()));
                    runwayElement.appendChild(obstacle);
                }
            }

            // Write the content into an XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));

            transformer.transform(source, result);
        } catch (TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    // Helper to add a text element
    private static void addTextElement(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(textContent));
        parent.appendChild(element);
    }

    // Helper method to safely get text content from XML elements
    private String getTextContent(Element element, String tag) {
        NodeList nodeList = element.getElementsByTagName(tag);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : "Unknown";
    }

    private ImageView getLastPlacedObstacle() {
        List<Node> children = runwayGroup.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i) instanceof ImageView) {
                return (ImageView) children.get(i);
            }
        }
        return null;
    }

    private static final double STOPWAY_SCALE_FACTOR = 10.0;
    public double getObstacleDistanceFromRunway() {
        Rectangle runwayRect = (Rectangle) runwayGroup.lookup("#runway");
        if (runwayRect == null) {
            System.out.println("Runway rectangle not found");
            return -1;
        }

        ImageView lastObstacle = getLastPlacedObstacle();
        if (lastObstacle == null) {
            System.out.println("No obstacles found");
            return -1;
        }

        Bounds runwayBounds = runwayRect.getBoundsInParent();
        Bounds obstacleBounds = lastObstacle.getBoundsInParent();

        double runwayStartX = runwayBounds.getMinX();
        double runwayStartY = runwayBounds.getMinY();

        double obstacleStartX = obstacleBounds.getMinX();
        double obstacleStartY = obstacleBounds.getMinY();

        double runwayAngle = runwayRect.getRotate();

        double dx = obstacleStartX - runwayStartX;
        double dy = obstacleStartY - runwayStartY;

        double projectedDistance = dx * Math.cos(Math.toRadians(runwayAngle)) +
                dy * Math.sin(Math.toRadians(runwayAngle));

        double realDistance = projectedDistance / STOPWAY_SCALE_FACTOR;

        System.out.println("Rendered Distance: " + projectedDistance + "px");
        System.out.println("Real Distance: " + realDistance + " actual units");

        return realDistance;
    }






    private int getIntContent(Element element, String tag) {
        try {
            return Integer.parseInt(getTextContent(element, tag));
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format for " + tag);
            return 0;
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
                    return new Obstacle(name, height, distance, "/images/topDown/car.png", 500);

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
            addUsers.setOnAction(event->{
                try {
                    addUser();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }); //TODO
            menuItem.getItems().add(addUsers);
            controlMenu.getMenus().add(menuItem);
        }
    }
    public void addUser() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateNewUserView.fxml"));
        Parent root = loader.load();

        CreateNewUserController controller = loader.getController();

        Stage stage = new Stage();
        controller.setStage(stage);
        stage.setTitle("Create New User");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        if(!(isEditor()||isAdmin())){
            addObstacleButton.setDisable(true);
            obstacleComboBox.setDisable(true);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

