package group50.graphics;

import group50.model.Runway;

import java.util.*;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class RunwayRenderer {
    public static final double layerOpacity = 0.3;
    public static final int SIDE_ON_GROUND_WIDTH = 600;
    Runway runway;
    static Paint TORA= Color.DEEPSKYBLUE;
    static Paint TODA= Color.CHARTREUSE;
    static Paint RESA= Color.RED;
    static Paint LDA= Color.MOCCASIN;
    static Paint ASDA= Color.ORANGE;
    static Paint CLEARWAY= Color.FUCHSIA;
    static  Paint CAGAREA= Color.ROYALBLUE;



    public static List<Node> generateTopDownRunway(Runway runwayInput ){
        runwayInput.redeclareALL();
        List<Node> objects = new ArrayList<>();
        Image runwayImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/158.jpg")));
        ImagePattern runwayPattern = new ImagePattern(
                runwayImage,
                0, 0,                         // Pattern anchor at top-left
                runwayImage.getWidth(),        // The repeating tile width
                runwayImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        Rectangle runwayRect= new Rectangle(runwayInput.getLength()*10,600);
        runwayRect.setFill(runwayPattern);

        Image concreteImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/conc2.jpg")));
        ImagePattern concretePattern = new ImagePattern(
                concreteImage,
                0, 0,                         // Pattern anchor at top-left
                concreteImage.getWidth(),        // The repeating tile width
                concreteImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        Rectangle stopwayRect= new Rectangle(runwayInput.getLength()*10+runwayInput.getStopway()*10*2,600);
        stopwayRect.setFill(concretePattern);
        Image grassImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/gg.jpeg")));
        ImagePattern grassPattern = new ImagePattern(
                grassImage,
                0, 0,                         // Pattern anchor at top-left
                grassImage.getWidth(),        // The repeating tile width
                grassImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        Ellipse grassArea= new Ellipse(stopwayRect.getWidth()+1000,stopwayRect.getWidth()+900);
        grassArea.setFill(grassPattern);

        Rectangle toraRect= new Rectangle(runwayInput.getTORA()*10,600, TORA);
        toraRect.setOpacity(layerOpacity);
        Rectangle todaRect= new Rectangle(runwayInput.getTODA()*10,600, TODA);
        todaRect.setOpacity(layerOpacity);
        Rectangle resaRect= new Rectangle(runwayInput.getRESA()*10,600, RESA);
        resaRect.setOpacity(layerOpacity);
        Rectangle ldaRect= new Rectangle(runwayInput.getLDA()*10,600, LDA);
        ldaRect.setOpacity(layerOpacity);
        Rectangle asdaRect= new Rectangle(runwayInput.getASDA()*10,600, ASDA);
        asdaRect.setOpacity(layerOpacity);
        Rectangle clearwayRect= new Rectangle(runwayInput.getClearwayLength()*10,runwayInput.getClearwayWidth()*10,CLEARWAY);
        clearwayRect.setOpacity(layerOpacity);
        Rectangle CAGRect= new Rectangle(runwayInput.getClearedAndGradedLengthBeyondRunwayEnds()*10*2+runwayRect.getWidth(),runwayInput.getClearedAndGradedWidth()*10,CAGAREA);
        CAGRect.setOpacity(layerOpacity);

        runwayRect.setLayoutX(grassArea.getCenterX() - runwayRect.getWidth() / 2);
        runwayRect.setLayoutY(grassArea.getCenterY() - runwayRect.getHeight() / 2);

        stopwayRect.setLayoutX(runwayRect.getLayoutX()-runwayInput.getStopway()*10);
        stopwayRect.setLayoutY(runwayRect.getLayoutY());


        todaRect.setLayoutX(runwayRect.getLayoutX());
        todaRect.setLayoutY(runwayRect.getLayoutY());
        if(runwayInput.getObstacle().getDistance()>runwayInput.getLength()/2){
            ldaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getDisplacedThreshold()*10);
            ldaRect.setLayoutY(runwayRect.getLayoutY());

            toraRect.setLayoutX(runwayRect.getLayoutX());
            toraRect.setLayoutY(runwayRect.getLayoutY());
            todaRect.setLayoutX(runwayRect.getLayoutX());
            todaRect.setLayoutY(runwayRect.getLayoutY());
            asdaRect.setLayoutX(runwayRect.getLayoutX());
            asdaRect.setLayoutY(runwayRect.getLayoutY());
        }else{
            if(runwayInput.getObstacle().getDistance()<runwayInput.getDisplacedThreshold()){
                ldaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getDisplacedThreshold()*10);
                ldaRect.setLayoutY(runwayRect.getLayoutY());
            }else {
                ldaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getObstacle().getDistance()*10);
                ldaRect.setLayoutY(runwayRect.getLayoutY());
            }
            toraRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getObstacle().getDistance()*10);
            toraRect.setLayoutY(runwayRect.getLayoutY());

            todaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getObstacle().getDistance()*10);
            todaRect.setLayoutY(runwayRect.getLayoutY());
            asdaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getObstacle().getDistance()*10);
            asdaRect.setLayoutY(runwayRect.getLayoutY());
        }





        clearwayRect.setLayoutX(toraRect.getLayoutX()+toraRect.getWidth());
        clearwayRect.setLayoutY(toraRect.getLayoutY()-((clearwayRect.getHeight()-toraRect.getHeight())/2));
        CAGRect.setLayoutX(stopwayRect.getLayoutX()-(CAGRect.getWidth()-stopwayRect.getWidth())/2);
        CAGRect.setLayoutY(stopwayRect.getLayoutY()-(CAGRect.getHeight()-stopwayRect.getHeight())/2);
        objects.addAll(Arrays.asList(grassArea,CAGRect,stopwayRect, runwayRect, ldaRect, toraRect, todaRect, asdaRect,clearwayRect));
        if(runwayInput.hasObstacle()){
            Image obstacleImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/topDown/"+runwayInput.getObstacle().getPath())));
            ImagePattern obstaclePattern= new ImagePattern(
                    obstacleImage,
                    0, 0,         // Start painting at top-left
                    1, 1,         // Cover whole rectangle (because proportional = true)
                    true          // proportional = true so it stretches nicely
            );

            double width = obstacleImage.getWidth();
            double height = obstacleImage.getHeight();
            Rectangle obstacle= new Rectangle(runwayInput.getObstacle().getScale(),(obstacleImage.getHeight() / obstacleImage.getWidth()) * runwayInput.getObstacle().getScale());
            obstacle.setFill(obstaclePattern);
            int distance= runwayInput.getObstacle().getDistance();
            int offset= (int) Math.max(obstacle.getWidth(),obstacle.getHeight());
            if(distance>runwayInput.getLength()/2){
                distance+=offset;
                System.out.println(runwayInput.getObstacle().getDistance());
                System.out.println("OFFEST: "+offset);
                obstacle.setLayoutX(runwayRect.getLayoutX()+runwayInput.getObstacle().getDistance()*10+offset);
            }else{
                distance-=offset;
                obstacle.setLayoutX(runwayRect.getLayoutX()+runwayInput.getObstacle().getDistance()*10-offset);
            }
            System.out.println("RUNWAY RENDERED OBSTACLE X: "+obstacle.getLayoutX()+" TRUE DISTANCE: "+runwayInput.getObstacle().getDistance() );
            obstacle.setLayoutY(runwayRect.getLayoutY() + (runwayRect.getHeight() / 2) - (obstacle.getHeight() / 2));
            obstacle.setRotate(-90);
            objects.add(obstacle);
        }


        runwayRect.setId("runway");
        stopwayRect.setId("stopway");
        grassArea.setId("grass");
        toraRect.setId("tora");
        todaRect.setId("toda");
        resaRect.setId("resa");
        ldaRect.setId("lda");
        asdaRect.setId("asda");
        clearwayRect.setId("clearway");
        CAGRect.setId("CAG");
        //3drawImageOnRunway(objects,"/images/plane.png",10);
        return objects;

    }

    public static List<Node> generateSideOnRunway(Runway runwayInput){

        List<Node> objects = new ArrayList<>();
        Image runwayImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/tarmac.jpg")));
        ImagePattern runwayPattern = new ImagePattern(
                runwayImage,
                0, 0,                         // Pattern anchor at top-left
                runwayImage.getWidth(),        // The repeating tile width
                runwayImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        Rectangle runwayRect= new Rectangle(runwayInput.getLength()*10, SIDE_ON_GROUND_WIDTH);
        runwayRect.setFill(runwayPattern);

        Image concreteImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/conc2.jpg")));
        ImagePattern concretePattern = new ImagePattern(
                concreteImage,
                0, 0,                         // Pattern anchor at top-left
                concreteImage.getWidth(),        // The repeating tile width
                concreteImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        Rectangle stopwayRect= new Rectangle(runwayInput.getLength()*10+runwayInput.getStopway()*10*2, SIDE_ON_GROUND_WIDTH);
        stopwayRect.setFill(concretePattern);
        Image grassImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/gg.jpeg")));
        ImagePattern grassPattern = new ImagePattern(
                grassImage,
                0, 0,                         // Pattern anchor at top-left
                grassImage.getWidth(),        // The repeating tile width
                grassImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );

        Rectangle grassArea= new Rectangle(runwayInput.getLength()*10*2, SIDE_ON_GROUND_WIDTH);
        grassArea.setFill(grassPattern);
        Image skyboxImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/sky2.png")));
        ImagePattern skyBoxPattern = new ImagePattern(
                skyboxImage,
                0, 0,         // Start from top-left
                1, 1,         // Scale it to cover the full rectangle (proportional)
                true          // Use proportional scaling
        );

        Rectangle skyBoxRect= new Rectangle(grassArea.getWidth(), SIDE_ON_GROUND_WIDTH*30);
        skyBoxRect.setFill(skyBoxPattern);

        Rectangle ldaRect= new Rectangle(runwayInput.getLDA()*10,SIDE_ON_GROUND_WIDTH*10,LDA);
        ldaRect.setOpacity(layerOpacity);
        Rectangle resaRect= new Rectangle(runwayInput.getRESA()*10,SIDE_ON_GROUND_WIDTH*10,RESA);
        resaRect.setOpacity(layerOpacity);
        runwayRect.setLayoutX((grassArea.getWidth() - runwayRect.getWidth()) / 2);
        runwayRect.setLayoutY((grassArea.getHeight() - runwayRect.getHeight()) / 2);
        stopwayRect.setLayoutX(runwayRect.getLayoutX()-runwayInput.getStopway()*10);
        stopwayRect.setLayoutY(runwayRect.getLayoutY());
        skyBoxRect.setLayoutX(grassArea.getLayoutX());
        skyBoxRect.setLayoutY(grassArea.getLayoutY()-skyBoxRect.getHeight());

        resaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getLength()*10);
        resaRect.setLayoutY(stopwayRect.getLayoutY()-resaRect.getHeight());

        ldaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getDisplacedThreshold()*10);
        ldaRect.setLayoutY(runwayRect.getLayoutY()-ldaRect.getHeight());
        if(runwayInput.getObstacle().getDistance()>runwayInput.getLength()/2){
            ldaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getDisplacedThreshold()*10);


        }else{
            if(runwayInput.getObstacle().getDistance()<runwayInput.getDisplacedThreshold()){
                ldaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getDisplacedThreshold()*10);
            }else {
                ldaRect.setLayoutX(runwayRect.getLayoutX()+runwayInput.getObstacle().getDistance()*10);
            }
        }
        double angleDegrees = runwayInput.getALS().getAngle(); // Example: 1.15 degrees for a 50:1 slope
        double angleRadians = Math.toRadians(angleDegrees);

// Horizontal length you want
        double alsLength = 9000; // e.g., 3000 meters

// Calculate vertical height based on angle
        double alsHeight = Math.tan(angleRadians) * alsLength;

// Start point (where LDA begins)
        double alsStartX = ldaRect.getLayoutX()+9000;
        double alsStartY = runwayRect.getLayoutY();

// Now build the ALS triangle
        Polygon alsTriangle = new Polygon();
        alsTriangle.getPoints().addAll(
                alsStartX, alsStartY,
                alsStartX - alsLength, alsStartY - alsHeight,
                alsStartX - alsLength, alsStartY
        );

        alsTriangle.setFill(Color.RED);
        alsTriangle.setOpacity(layerOpacity);





        objects.addAll(Arrays.asList(skyBoxRect,grassArea,stopwayRect,runwayRect,resaRect,ldaRect,alsTriangle));
        if(runwayInput.hasObstacle()){
            Image obstacleImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/sideOn/"+runwayInput.getObstacle().getPath())));
            ImagePattern obstaclePattern= new ImagePattern(
                    obstacleImage,
                    0, 0,         // Start painting at top-left
                    1, 1,         // Cover whole rectangle (because proportional = true)
                    true          // proportional = true so it stretches nicely
            );

            double width = obstacleImage.getWidth();
            double height = obstacleImage.getHeight();
            Rectangle obstacle= new Rectangle(runwayInput.getObstacle().getScale(),(obstacleImage.getHeight() / obstacleImage.getWidth()) * runwayInput.getObstacle().getScale());
            obstacle.setFill(obstaclePattern);
            obstacle.setLayoutX(runwayRect.getLayoutX()+runwayInput.getObstacle().getDistance()*10-(obstacle.getWidth()));
            System.out.println("RUNWAY RENDERED OBSTACLE X: "+obstacle.getLayoutX()+" TRUE DISTANCE: "+runwayInput.getObstacle().getDistance() );
            obstacle.setLayoutY(runwayRect.getLayoutY() - obstacle.getHeight());

            objects.add(obstacle);
        }
        runwayRect.setId("runway");
        stopwayRect.setId("stopway");
        grassArea.setId("grass");
        skyBoxRect.setId("skyBox");
        ldaRect.setId("lda");
        resaRect.setId("resa");
        alsTriangle.setId("als");
        return objects;
    }

    public static void drawImageOnRunway(List<Node> objects, String imagePath, double distanceX) {
        Image image = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);

        // Find the runway rectangle in the list of objects
        Rectangle runway = null;
        for (Node node : objects) {
            if (node.getId() != null && node.getId().equals("runway")) {
                runway = (Rectangle) node;
                break;
            }
        }

        if (runway != null) {
            // Calculate the X position based on the runway's layoutX and the provided distance
            double imageX = runway.getLayoutX() + distanceX * 10; // Assuming 1 unit in distanceX is 10 pixels

            // Position the ImageView
            imageView.setLayoutX(imageX);
            imageView.setLayoutY(runway.getLayoutY() + (runway.getHeight() - image.getHeight()) / 2); // Center vertically on the runway

            // Add the ImageView to the list of objects
            objects.add(imageView);
        } else {
            System.err.println("Runway rectangle not found. Cannot draw image.");
        }
    }
}