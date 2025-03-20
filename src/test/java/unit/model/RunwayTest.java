package unit.model;
import group50.model.Obstacle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import group50.model.Runway;

@DisplayName("Runway Redeclaration Tests")
class RunwayTest {

    Runway runway;


    final String RUNWAY_NAME = "09L";
    final int LENGTH = 3902;
    final int STRIP_LENGTH = 3962;
    final int STOPWAY = 0;
    final int CLEARWAY_LENGTH = 60;
    final int CLEARWAY_WIDTH = 150;
    final int DISPLACED_THRESHOLD = 306;
    final int RESA = 240;

    final int BLAST_PROTECTION = 300;
    final String  OBSTACLE_NAME = "name";
    final int OBSTACLE_DISTANCE = 500;
    final int OBSTACLE_HEIGHT = 25;
    final int STRIP_END_OFFSET = 60;
    final String  OBSTACLE_PATH = "PATH";
    final int OBSTACLE_SCALE = 500;
    @Test
    @DisplayName("it should correctly initialize a Runway with given parameters")
    void itShouldInitializeRunway() {
        String name="09L";
        int length=8000;
        int stripLength=8000;
        int stopway=100;
        int clearwayLength=75;
        int clearwayWidth=100;
        int displacedThreshold=0;
        int RESA=120;
        Runway runway = new Runway(name,length,stripLength,stopway,clearwayLength,clearwayWidth,displacedThreshold,RESA);


        assertEquals("09L", runway.getName());
        assertEquals(length, runway.getTORA());
        assertEquals(length+clearwayLength, runway.getTODA());
        assertEquals(length + stopway, runway.getASDA());
        assertEquals( length - displacedThreshold, runway.getLDA());
    }

    @Nested
    @DisplayName("When recalculating without an obstacle")
    class WithoutObstacle {

        @BeforeEach
        void setup() {
            runway = new Runway(RUNWAY_NAME, LENGTH, STRIP_LENGTH, STOPWAY, CLEARWAY_LENGTH, CLEARWAY_WIDTH, DISPLACED_THRESHOLD, RESA);
            runway.applyManualParameters(runway, LENGTH, CLEARWAY_LENGTH, STOPWAY, DISPLACED_THRESHOLD);
            runway.redeclareALL();
        }

        @Test
        @DisplayName("should have correct TORA")
        void testTORA() {
            assertEquals(LENGTH, runway.getTORA());
        }

        @Test
        @DisplayName("should have correct TODA")
        void testTODA() {
            assertEquals(LENGTH + CLEARWAY_LENGTH, runway.getTODA());
        }

        @Test
        @DisplayName("should have correct ASDA")
        void testASDA() {
            assertEquals(LENGTH, runway.getASDA());
        }

        @Test
        @DisplayName("should have correct LDA")
        void testLDA() {
            assertEquals(LENGTH - DISPLACED_THRESHOLD, runway.getLDA());
        }
    }

    @Nested
    @DisplayName("When recalculating with an obstacle")
    class WithObstacle {

        @BeforeEach
        void setup() {
            runway = new Runway(RUNWAY_NAME, LENGTH, STRIP_LENGTH, STOPWAY, CLEARWAY_LENGTH, CLEARWAY_WIDTH, DISPLACED_THRESHOLD, RESA);
            runway.setBlastProtection(BLAST_PROTECTION);
            runway.setObstacle(new Obstacle(OBSTACLE_NAME,OBSTACLE_HEIGHT,OBSTACLE_DISTANCE, OBSTACLE_PATH, OBSTACLE_SCALE));
            runway.redeclareALL();

        }

        @Test
        @DisplayName("should recalculate TORA correctly")
        void testTORAWithObstacle() {
            int expectedTORA = LENGTH - OBSTACLE_DISTANCE - BLAST_PROTECTION;
            assertEquals(expectedTORA, runway.getTORA());
        }

        @Test
        @DisplayName("should recalculate TODA correctly")
        void testTODAWithObstacle() {
            int expectedTODA = STRIP_LENGTH - OBSTACLE_DISTANCE - BLAST_PROTECTION;
            assertEquals(expectedTODA, runway.getTODA());
        }

        @Test
        @DisplayName("should recalculate ASDA correctly")
        void testASDAWithObstacle() {
            int expectedASDA = LENGTH - OBSTACLE_DISTANCE - BLAST_PROTECTION;
            assertEquals(expectedASDA, runway.getASDA());
        }

        @Test
        @DisplayName("should recalculate LDA correctly")
        void testLDAWithObstacle() {
            int expectedLDA = 1852;
            assertEquals(expectedLDA, runway.getLDA());
        }
    }
}
