package group50.model;

import java.util.List;

public class Airport {

  public Airport(String name) {
    this.name = name;
  }

  private String name;

  private List<Runway> runways;

  public void addRunway(String name, int TORA, int TODA, int ASDA, int LDA, int clearwayWidth,int clearwayHeight) {
    runways.add(new Runway(name, TORA, TODA, ASDA, LDA,clearwayWidth,clearwayHeight,1));
  }

  public List<Runway> getRunways() {
    return runways;
  }
}
