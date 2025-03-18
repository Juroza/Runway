package group50.model;

public class Obstacle {

  public Obstacle(String name, int height, int distance) {
    this.name = name;
    this.height = height;
    this.distance = distance;
  }

  private String name;
  private int height;
  //positive distances are from the start of the runway.
  //negative distances are from the end of the runway.
  private int distance;


  public String getName() { return name; }
  public int getHeight() {
    return height;
  }
  public int getDistance() {
    return distance;
  }

  public String toString() {
    return name + " (H: " + height + "m, D: " + distance + "m)";
  }
}
