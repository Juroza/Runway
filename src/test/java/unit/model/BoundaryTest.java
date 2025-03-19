package unit.model;

import group50.model.Obstacle;
import group50.model.Runway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Runway Boundary Tests")
public class BoundaryTest {
    @Test
    @DisplayName("should handle minimum runway length")
    void testMinimumRunwayLength() {
        Runway runway = new Runway("09L", 100, 100, 0, 0, 0, 0, 60);  // 100m min
        assertEquals(100, runway.getTORA());
        assertEquals(100, runway.getTODA());
        assertEquals(100, runway.getASDA());
        assertEquals(100, runway.getLDA());
    }

    @Test
    @DisplayName("should handle maximum runway length")
    void testMaximumRunwayLength() {
        Runway runway = new Runway("09L", 10000, 10000, 500, 500, 500, 1000, 240);  // 10000m max
        assertEquals(10000, runway.getTORA());
        assertEquals(10500, runway.getTODA());
        assertEquals(10500, runway.getASDA());
        assertEquals(9000, runway.getLDA());
    }


    @Test
    @DisplayName("should handle zero-length runway")
    void testZeroLengthRunway() {
        Runway runway = new Runway("09L", 0, 0, 0, 0, 0, 0, 240);
        assertEquals(0, runway.getTORA());
        assertEquals(0, runway.getTODA());
        assertEquals(0, runway.getASDA());
        assertEquals(0, runway.getLDA());
    }

    @Test
    @DisplayName("TODA should be greater than or equal to TORA")
    void testTODA() {
        Runway runway = new Runway("09L", 3000, 3200, 200, 400, 500, 300, 240);
        assertTrue(runway.getTODA() >= runway.getTORA(), "TODA must be at least TORA");
    }

    @Test
    @DisplayName("ASDA should be greater than or equal to TORA")
    void testASDA() {
        Runway runway = new Runway("09L", 3000, 3200, 200, 400, 500, 300, 240);
        assertTrue(runway.getASDA() >= runway.getTORA(), "ASDA must be at least TORA");
    }

    @Test
    @DisplayName("LDA should be less than or equal to TORA")
    void testLDA() {
        Runway runway = new Runway("09L", 3000, 3200, 200, 400, 500, 300, 240);
        assertTrue(runway.getLDA() <= runway.getTORA(), "LDA must not exceed TORA");
    }

    @Test
    @DisplayName("LDA should be correctly calculated")
    void testLDACalculation() {
        Runway runway = new Runway("09L", 3000, 3200, 200, 400, 500, 300, 240);
        assertEquals(runway.getTORA() - runway.getDisplacedThreshold(), runway.getLDA(),
                "LDA should be TORA - Displaced Threshold");
    }

    @Test
    @DisplayName("TORA should be greater than stopway and clearway")
    void testTORAValidity() {
        Runway runway = new Runway("09L", 3000, 3200, 500, 2000, 500, 300, 240);
        assertTrue(runway.getTORA() >= runway.getStopway(), "TORA must be greater than Stopway");
        assertTrue(runway.getTORA() >= runway.getClearwayLength(), "TORA must be greater than Clearway");
    }

}
