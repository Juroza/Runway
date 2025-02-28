package group50.model;

import static java.lang.Math.*;

public class Runway {

  public Runway(String name, int length, int stripLength, int stopway, int clearway, int displacedThreshold, int RESA) {
    this.name = name;
    this.length = length;
    this.stopway = stopway;
    this.clearway = clearway; //clearway should be greater than stopway
    this.TORA = length;
    this.ASDA = length + stopway;
    this.TODA = length + clearway;

    this.displacedThreshold = displacedThreshold;
    this.LDA = length - displacedThreshold;

    this.stripLength = stripLength;
    this.stripEnd = stripLength - length;
    this.RESA = max(240, RESA);

    this.ALS = new Surface(); //assumes plane's angle of descent is 50:1
    this.TOCS = new Surface();  //assumes plane's angle of ascent is 50:1

  }
  public Runway(){};

  private Obstacle obstacle = new Obstacle(0, 0);

  private Boolean hasObstacle() {
    return obstacle.getHeight() != 0;
  }

  public void redeclareALL() {
    TORA = calculateTORA();
    TODA = calculateTODA();
    ASDA = calculateASDA();
    LDA = calculateLDA();
  }

  private int calculateTORA() {
    if (obstacle.getDistance() < 0)
      return round(length - abs(obstacle.getDistance()) - 60 - (RESA + obstacle.getHeight())*TOCS.getAngle() - 0.5f);
    else
      return length - obstacle.getDistance() - blastProtection;
  }

  private int calculateASDA() {
    if (obstacle.getDistance() < 0)
      return round(length - abs(obstacle.getDistance()) - 60 - (RESA + obstacle.getHeight())*TOCS.getAngle() - 0.5f);
    else
      return length + clearway - obstacle.getDistance() - blastProtection;
  }

  private int calculateTODA() {
    if (obstacle.getDistance() < 0)
      return round(length - abs(obstacle.getDistance()) - 60 - (RESA + obstacle.getHeight())*TOCS.getAngle() - 0.5f);
    else
      return length + stopway - obstacle.getDistance() - blastProtection;
  }

  private int calculateLDA() {
    if (obstacle.getDistance() < 0)
      return length - max(abs(obstacle.getDistance()), displacedThreshold);
    else
      return round(length - obstacle.getDistance() - 60 - max((RESA + obstacle.getHeight())*TOCS.getAngle(), displacedThreshold) - 0.5f) - displacedThreshold;
  }
  

  private String name;

  private int length;

  private int stripLength;

  private int TORA; //take-off run available

  private int TODA; //take-off distance available

  private int ASDA; //accelerate-stop distance

  private int LDA; //landing distance available

  private int displacedThreshold; //can be used for take-off, but not landing

  private int clearway;  //beyond TORA, can be used for plane to climb to a certain height

  private int stopway;  //beyond TORA, can be used for an abandoned take-off

  private int RESA; //runway end safety area

  private int stripEnd; //distance between runway and stopway

  private int blastProtection;  //safety distance behind aircraft

  private Surface ALS;  //approach landing surface

  private Surface TOCS; //take-off climb surface

  public String getName() {
    return name;
  }

  public int getLength() {
    return length;
  }

  public int getStripLength() {
    return stripLength;
  }

  public int getTODA() {
    return TODA;
  }

  public int getTORA() {
    return TORA;
  }

  public int getASDA() {
    return ASDA;
  }

  public int getLDA() {
    return LDA;
  }

  public int getDisplacedThreshold() {
    return displacedThreshold;
  }

  public int getClarway() {
    return clearway;
  }

  public int getStopway() {
    return stopway;
  }

  public int getRESA() {
    return RESA;
  }

  public int getStripEnd() {
    return stripEnd;
  }

  public int getBlastProtection() {
    return blastProtection;
  }

  public Surface getALS() {
    return ALS;
  }

  public Surface getTOCS() {
    return TOCS;
  }

  public void setBlastProtection(int blastProtection) {
    this.blastProtection = blastProtection;
  }


  public void setLength(int length) {
    this.length=length;
  }

  public void setName(String name) {
    this.name=name;
  }
}
