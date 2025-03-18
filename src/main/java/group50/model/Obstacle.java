package group50.model;

public class Obstacle {

  public Obstacle(String name, int height, int distance, String path, Integer scale) {
    this.name = name;
    this.height = height;
    this.distance = distance;
    this.path = path;
    this.scale = scale;
  }

  private String name;
  private int height;

  private int distance; //distance is measured from near end of the runway
private String path;
private Integer scale;

  public String getName() { return name; }
  public int getHeight() {
    return height;
  }
  public int getDistance() {
    return distance;
  }
  public void setHeight(int heightInput) {
    height=heightInput;
  }
  public void setDistance(int distanceInput) {
     distance=distanceInput;
  }

  public String toString() {
    return name + " (H: " + height + "m, D: " + distance + "m)";
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Integer getScale() {
    return scale;
  }

  public void setScale(Integer scale) {
    this.scale = scale;
  }
}
