package group50.model;

public class Obstacle {

  public Obstacle(int id, int height, int distance) {
    this.id = id;
    this.height = height;
    this.distance = distance;
  }

  private int id;
  private int height;
  //positive distances are from the start of the runway.
  // negative distances are from the end of the runway.
  private int distance;


  public int getId() { return id; }
  public int getHeight() {
    return height;
  }
  public int getDistance() {
    return distance;
  }

  public String toString() {
    return id + " (H: " + height + "m, D: " + distance + "m)";
  }
}
