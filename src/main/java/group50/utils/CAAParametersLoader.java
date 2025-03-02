package group50.utils;

import group50.model.Runway;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CAAParametersLoader {
    public static List<Runway> readAllRunwaysFromXML(String filePath) {
        List<Runway> runways = new ArrayList<>();

        try {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList runwayNodes = doc.getElementsByTagName("Runway");

            for (int i = 0; i < runwayNodes.getLength(); i++) {
                Element runwayElement = (Element) runwayNodes.item(i);
                Runway runway = new Runway();

                runway.setLength(Integer.parseInt(runwayElement.getElementsByTagName("length").item(0).getTextContent()));
                runway.setName(runwayElement.getElementsByTagName("Name").item(0).getTextContent());
                runway.setBlastProtection(Integer.parseInt(runwayElement.getElementsByTagName(("BlastProtection")).item(0).getTextContent()));
                runways.add(runway);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return runways;
    }

    public static void applyManualParameters (Runway runway, int length, int clearwayLength, int stopway, int displacedThreshold){
        runway.setLength(length);
        runway.getClearedAndGradedWidth();
        runway.getClearedAndGradedLengthBeyondRunwayEnds();
        runway.setBlastProtection(displacedThreshold);

        runway.redeclareALL();

    }
}
