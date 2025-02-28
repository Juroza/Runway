package group50.model;

public class Obstacle {

  public Obstacle(int height, int distance) {
    this.height = height;
    this.distance = distance;
  }

  private int height;


  //positive distances are from the start of the runway.
  // negative distances are from the end of the runway.
  private int distance;


  public int getHeight() {
    return height;
  }

  public int getDistance() {
    return distance;
  }
}
