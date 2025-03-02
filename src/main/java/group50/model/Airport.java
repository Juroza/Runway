package group50.model;

import java.util.List;

public class Airport {

  public Airport(String name) {
    this.name = name;
  }

  private String name;

  private List<Runway> runways;

  public int getClearedAndGradedWidth() {
    return clearedAndGradedWidth;
  }

  public void setClearedAndGradedWidth(int clearedAndGradedWidth) {
    this.clearedAndGradedWidth = clearedAndGradedWidth;
  }

  private int clearedAndGradedWidth;  // Total width of the cleared and graded area (meters)

  public int getClearedAndGradedLengthBeyondRunwayEnds() {
    return clearedAndGradedLengthBeyondRunwayEnds;
  }

  public void setClearedAndGradedLengthBeyondRunwayEnds(int clearedAndGradedLengthBeyondRunwayEnds) {
    this.clearedAndGradedLengthBeyondRunwayEnds = clearedAndGradedLengthBeyondRunwayEnds;
  }

  private int clearedAndGradedLengthBeyondRunwayEnds;  // Length it extends beyond each end of the runway (meters)

  public void addRunway(String name, int TORA, int TODA, int ASDA, int LDA, int clearwayWidth,int clearwayHeight) {
    runways.add(new Runway(name, TORA, TODA, ASDA, LDA,clearwayWidth,clearwayHeight,1));
  }

}
