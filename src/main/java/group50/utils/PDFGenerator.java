package group50.utils;

import group50.model.Obstacle;
import group50.model.Runway;
import javafx.concurrent.Task;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterJob;
import java.io.File;
import java.util.List;

public class PDFGenerator {
    public static void printPDF(String airportName, List<Runway> runwayList, String userName) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    PDDocument document = new PDDocument();
                    PDPage page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    float margin = 50;
                    float yStart = PDRectangle.A4.getHeight() - margin;
                    float yPosition = yStart;

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(airportName);
                    yPosition -= 50;

                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);

                    for (Runway runway : runwayList) {

                        if (yPosition < 100) {
                            contentStream.endText();
                            contentStream.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            contentStream = new PDPageContentStream(document, page);
                            contentStream.beginText();
                            yPosition = yStart;
                            contentStream.newLineAtOffset(margin, yPosition);
                        }

                        contentStream.newLineAtOffset(0, -50);
                        yPosition -= 50;
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                        contentStream.showText("Runway: " + runway.getName());
                        contentStream.newLineAtOffset(0, -30);
                        yPosition -= 30;

                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                        contentStream.showText("Thresholds:");
                        contentStream.newLineAtOffset(0, -20);
                        yPosition -= 20;

                        contentStream.setFont(PDType1Font.HELVETICA, 12);
                        contentStream.showText("TORA: " + runway.getTORA());
                        contentStream.newLineAtOffset(0, -20);
                        yPosition -= 20;
                        contentStream.showText("TODA: " + runway.getTODA());
                        contentStream.newLineAtOffset(0, -20);
                        yPosition -= 20;
                        contentStream.showText("LDA: " + runway.getLDA());
                        contentStream.newLineAtOffset(0, -20);
                        yPosition -= 20;
                        contentStream.showText("ASDA: " + runway.getASDA());
                        contentStream.newLineAtOffset(0, -20);
                        yPosition -= 20;
                        contentStream.showText("RESA: " + runway.getRESA());
                        contentStream.newLineAtOffset(0, -30);
                        yPosition -= 30;

                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                        contentStream.showText("Obstacles:");
                        contentStream.newLineAtOffset(0, -20);
                        yPosition -= 20;

                        if (!runway.getObstacle().getName().equals("name")) {
                            contentStream.setFont(PDType1Font.HELVETICA, 12);
                            contentStream.showText("Obstacle found on Runway: " + runway.getObstacle().getName());
                            contentStream.newLineAtOffset(0, -20);
                            yPosition -= 20;
                            contentStream.showText("Obstacle height: " + runway.getObstacle().getHeight());
                            contentStream.newLineAtOffset(0, -20);
                            yPosition -= 20;
                            contentStream.showText("Obstacle Distance on runway (meters): " + runway.getObstacle().getDistance());
                            contentStream.newLineAtOffset(0, -30);
                            yPosition -= 30;
                        } else {
                            contentStream.setFont(PDType1Font.HELVETICA, 12);
                            contentStream.showText("No Obstacles Found on Runway " + runway.getName());
                            contentStream.newLineAtOffset(0, -30);
                            yPosition -= 30;
                        }
                        if(isLandingSafe(runway,runway.getObstacle(),2200)){
                            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                            contentStream.showText("LIKELY SAFE TO LAND");
                            contentStream.newLineAtOffset(0, -30);
                        }else{
                            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                            contentStream.showText("LIKELY UNSAFE TO LAND --- FURTHER ASSESSMENT REQUIRED");
                            contentStream.newLineAtOffset(0, -30);
                        }
                        if(isTakeoffSafe(runway,runway.getObstacle(),2500)){
                            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                            contentStream.showText("LIKELY SAFE TO TAKEOFF");
                            contentStream.newLineAtOffset(0, -30);
                        }else{
                            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                            contentStream.showText("LIKELY UNSAFE TO TAKEOFF --- FURTHER ASSESSMENT REQUIRED");
                            contentStream.newLineAtOffset(0, -30);
                        }
                    }


                    contentStream.setFont(PDType1Font.TIMES_ITALIC, 16);
                    contentStream.showText("Reported by: " + userName);

                    contentStream.endText();
                    contentStream.close();


                    File file = new File("store.pdf");
                    document.save(file);
                    document.close();

                    PDDocument documentRead = PDDocument.load(file);
                    PrinterJob job = PrinterJob.getPrinterJob();
                    job.setPageable(new PDFPageable(documentRead));

                    if (job.printDialog()) {
                        job.print();
                    }
                    documentRead.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    private static boolean isLandingSafe(Runway runway, Obstacle obstacle, int requiredLandingDistance) {

        int lda = runway.getLDA();
        int displacedThreshold = runway.getDisplacedThreshold();
        int RESA = runway.getRESA();
        int stripEnd = 60;
        int alsSlope = 50;


        if (obstacle == null || obstacle.getHeight() == 0) {
            return lda >= requiredLandingDistance;
        }


        int obstacleDistance = obstacle.getDistance();
        int obstacleHeight = obstacle.getHeight();


        if (obstacleDistance < runway.getLength() / 2) {

            int requiredClearance = obstacleHeight * alsSlope;
            int newLDA = (int) (runway.getLength() - obstacleDistance - stripEnd - requiredClearance);


            return newLDA >= requiredLandingDistance;
        }
        else {

            int newLDA = (int) (obstacleDistance - RESA - stripEnd);


            return newLDA >= requiredLandingDistance;
        }
    }
    public static boolean isTakeoffSafe(Runway runway, Obstacle obstacle, int requiredTakeoffDistance) {

        int tora = runway.getTORA();
        int toda = runway.getTODA();
        int asda = runway.getASDA();
        int RESA = runway.getRESA();
        int stripEnd = 60;
        int tocsSlope = 50;


        if (obstacle == null || obstacle.getHeight() == 0) {
            return tora >= requiredTakeoffDistance;
        }


        int obstacleDistance = obstacle.getDistance();
        int obstacleHeight = obstacle.getHeight();


        if (obstacleDistance < tora / 2) {

            int requiredClearance = obstacleHeight * tocsSlope;
            int newTORA = (int) (tora - obstacleDistance - stripEnd - requiredClearance);


            int newTODA = newTORA;
            int newASDA = newTORA;

            return (newTORA >= requiredTakeoffDistance) && (newTODA >= requiredTakeoffDistance) && (newASDA >= requiredTakeoffDistance);
        } else {

            return (tora >= requiredTakeoffDistance) && (toda >= requiredTakeoffDistance) && (asda >= requiredTakeoffDistance);
        }
    }


}
