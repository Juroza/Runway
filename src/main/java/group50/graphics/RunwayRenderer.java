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
import javafx.scene.shape.Rectangle;

public class RunwayRenderer {
    public static final double layerOpacity = 0.2;
    Runway runway;
    static Paint TORA= Color.DEEPSKYBLUE;
    static Paint TODA= Color.CHARTREUSE;
    static Paint RESA= Color.RED;
    static Paint LDA= Color.MOCCASIN;
    static Paint ASDA= Color.ORANGE;


    public static List<Node> generateTopDownRunway(Runway runwayinput ){
        List<Node> objects = new ArrayList<>();
        Image runwayImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/158.jpg")));
        ImagePattern runwayPattern = new ImagePattern(
                runwayImage,
                0, 0,                         // Pattern anchor at top-left
                runwayImage.getWidth(),        // The repeating tile width
                runwayImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        Rectangle runwayRect= new Rectangle(runwayinput.getLength()*10,600);
        runwayRect.setFill(runwayPattern);

        Image concreteImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/conc2.jpg")));
        ImagePattern concretePattern = new ImagePattern(
                concreteImage,
                0, 0,                         // Pattern anchor at top-left
                concreteImage.getWidth(),        // The repeating tile width
                concreteImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        Rectangle stopwayRect= new Rectangle(runwayinput.getLength()*10+runwayinput.getStopway()*2,600);
        stopwayRect.setFill(concretePattern);
        Image grassImage = new Image(Objects.requireNonNull(RunwayRenderer.class.getResourceAsStream("/images/gg.jpeg")));
        ImagePattern grassPattern = new ImagePattern(
                grassImage,
                0, 0,                         // Pattern anchor at top-left
                grassImage.getWidth(),        // The repeating tile width
                grassImage.getHeight(),       // The repeating tile height
                false                         // Use absolute coordinates (not proportional)
        );
        Ellipse grassArea= new Ellipse(runwayinput.getLength()*10*2,600*50);
        grassArea.setFill(grassPattern);

        Rectangle toraRect= new Rectangle(runwayinput.getTORA()*10,600, TORA);
        toraRect.setOpacity(layerOpacity);
        Rectangle todaRect= new Rectangle(runwayinput.getTODA()*10,600, TODA);
        todaRect.setOpacity(layerOpacity);
        Rectangle resaRect= new Rectangle(runwayinput.getRESA()*10,600, RESA);
        resaRect.setOpacity(layerOpacity);
        Rectangle ldaRect= new Rectangle(runwayinput.getLDA()*10,600, LDA);
        ldaRect.setOpacity(layerOpacity);
        Rectangle asdaRect= new Rectangle(runwayinput.getASDA()*10,600, ASDA);
        asdaRect.setOpacity(layerOpacity);

        runwayRect.setLayoutX(grassArea.getCenterX() - runwayRect.getWidth() / 2);
        runwayRect.setLayoutY(grassArea.getCenterY() - runwayRect.getHeight() / 2);

        stopwayRect.setLayoutX(runwayRect.getLayoutX()-runwayinput.getStopway()*10);
        stopwayRect.setLayoutY(runwayRect.getLayoutY());
        toraRect.setLayoutX(runwayRect.getLayoutX());
        toraRect.setLayoutY(runwayRect.getLayoutY());
        todaRect.setLayoutX(runwayRect.getLayoutX());
        todaRect.setLayoutY(runwayRect.getLayoutY());
        ldaRect.setLayoutX(runwayRect.getLayoutX());
        ldaRect.setLayoutY(runwayRect.getLayoutY());
        asdaRect.setLayoutX(runwayRect.getLayoutX());
        asdaRect.setLayoutY(runwayRect.getLayoutY());
        objects.addAll(Arrays.asList(grassArea, stopwayRect, runwayRect, ldaRect, toraRect, todaRect, asdaRect));



        return objects;

    }
}
