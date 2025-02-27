package group50.model;

public class Surface {

  public Surface() {
    float horiz = 50;
    float vert = 1;
    angle = horiz / vert;
  }

  private float angle;  //angle of lift/descent expressed as a ratio

  public void setAngle(float a) {
    angle = a;
  }

  public float getAngle() {
    return angle;
  }

}
