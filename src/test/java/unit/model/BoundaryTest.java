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

}
