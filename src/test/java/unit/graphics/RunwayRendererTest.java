package unit.graphics;

import group50.model.Runway;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import group50.graphics.RunwayRenderer;
@DisplayName("RunwayRenderer Tests")
class RunwayRendererTest {

    Runway runway;
    final int RUNWAY_LENGTH = 3902;
    final int RUNWAY_WIDTH = 60;
    final int SCALE_FACTOR = 10;

    @BeforeEach
    void setup() {
        runway = new Runway(
                "09L",
                RUNWAY_LENGTH,
                3962, // Strip length
                0,    // Stopway
                60,   // Clearway length
                150,  // Clearway width
                306,  // Displaced threshold
                240   // RESA
        );
    }

    @Nested
    @DisplayName("Top-down view rendering")
    class TopDownRenderingTest {

        @Test
        @DisplayName("should render runway rectangle in correct scale")
        void testRunwayLengthInTopDownView() {
            List<Node> nodes = RunwayRenderer.generateTopDownRunway(runway);
            Rectangle runwayRect = (Rectangle) nodes.stream()
                    .filter(node -> "runway".equals(node.getId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Runway rectangle not found"));

            double expectedWidth = RUNWAY_LENGTH * SCALE_FACTOR;
            assertEquals(expectedWidth, runwayRect.getWidth(), "Runway width in top-down view is incorrect");
        }
    }

    @Nested
    @DisplayName("Side-on view rendering")
    class SideOnRenderingTest {

        @Test
        @DisplayName("should render runway rectangle in correct scale")
        void testRunwayLengthInSideOnView() {
            List<Node> nodes = RunwayRenderer.generateSideOnRunway(runway);
            Rectangle runwayRect = (Rectangle) nodes.stream()
                    .filter(node -> "runway".equals(node.getId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Runway rectangle not found"));

            double expectedWidth = RUNWAY_LENGTH * SCALE_FACTOR;
            assertEquals(expectedWidth, runwayRect.getWidth(), "Runway width in side-on view is incorrect");
        }
    }
}
