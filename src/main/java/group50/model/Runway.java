package group50.model;

import static java.lang.Math.*;

public class Runway {

  public Runway(String name, int length, int stripLength, int stopway, int clearwayLength,int clearwayWidth, int displacedThreshold, int RESA) {
    this.name = name;
    this.length = length;
    this.stopway = stopway;
    this.clearwayLength = clearwayLength; //clearway should be greater than stopway
    this.clearwayWidth= clearwayWidth;
    this.TORA = length;
    this.ASDA = length + stopway;
    this.TODA = length + clearwayLength;

    this.displacedThreshold = displacedThreshold;
    this.LDA = length - displacedThreshold;

    this.stripLength = stripLength;
    this.stripEnd = stripLength - length;
    this.RESA = max(240, RESA);

    this.ALS = new Surface(); //assumes plane's angle of descent is 50:1
    this.TOCS = new Surface();  //assumes plane's angle of ascent is 50:1

  }
  public Runway(){};

  private int STRIPENDOFFSET = 60;

  private Obstacle obstacle = new Obstacle("name",0, 0, "", 0);

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
    if (!hasObstacle()) return length;
    if (obstacle.getDistance() > length / 2) return round(length - (length - obstacle.getDistance()) - STRIPENDOFFSET - (RESA + obstacle.getHeight()*TOCS.getAngle()) - 0.5f);
    return length - obstacle.getDistance() - blastProtection;
  }

  private int calculateASDA() {
    if (!hasObstacle()) return length + stopway;
    if (obstacle.getDistance() > length / 2) return round(length - (length - obstacle.getDistance()) - STRIPENDOFFSET - (RESA + obstacle.getHeight()*TOCS.getAngle()) - 0.5f);
    return length + stopway - obstacle.getDistance() - blastProtection;
  }

  private int calculateTODA() {
    if (!hasObstacle()) return length + clearwayLength;
    if (obstacle.getDistance() > length / 2) return round(length - (length - obstacle.getDistance()) - STRIPENDOFFSET - (RESA + obstacle.getHeight()*TOCS.getAngle()) - 0.5f);
    return length + clearwayLength - obstacle.getDistance() - blastProtection;
  }

  private int calculateLDA() {
    if (!hasObstacle()) return length - displacedThreshold;
    if (obstacle.getDistance() > length / 2) {
      System.out.println(obstacle.getDistance()+"-"+displacedThreshold);
      return obstacle.getDistance() - displacedThreshold;
    }
    return round(length - obstacle.getDistance() - STRIPENDOFFSET - max((RESA + obstacle.getHeight()*ALS.getAngle()), displacedThreshold) - 0.5f);
  }



  private String name;

  private int length;

  private int stripLength;


  private int TORA; //take-off run available

  private int TODA; //take-off distance available

  private int ASDA; //accelerate-stop distance

  private int LDA; //landing distance available

  private int displacedThreshold; //can be used for take-off, but not landing

  private int clearwayLength;  //beyond TORA, can be used for plane to climb to a certain height

  public int getClearedAndGradedWidth() {
    return clearedAndGradedWidth;
  }

  public void setClearedAndGradedWidth(int clearedAndGradedWidth) {
    this.clearedAndGradedWidth = clearedAndGradedWidth;
  }

  public int getClearedAndGradedLengthBeyondRunwayEnds() {
    return clearedAndGradedLengthBeyondRunwayEnds;
  }

  public void setClearedAndGradedLengthBeyondRunwayEnds(int clearedAndGradedLengthBeyondRunwayEnds) {
    this.clearedAndGradedLengthBeyondRunwayEnds = clearedAndGradedLengthBeyondRunwayEnds;
  }

  private int clearedAndGradedWidth;  // Total width of the cleared and graded area (meters)
  private int clearedAndGradedLengthBeyondRunwayEnds;  // Length it extends beyond each end of the runway (meters)

  public Obstacle getObstacle() {
    return obstacle;
  }

  public int getClearwayLength() {
    return clearwayLength;
  }

  public int getClearwayWidth() {
    return clearwayWidth;
  }

  private int clearwayWidth; //beyond TORA, must be at least as wide as the runway, though it may be wider depending on regulations and airport design.
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

  public String toString() {
    return name;
  }

  public void setObstacle(Obstacle obstacle) {
    this.obstacle = obstacle;
  }

  public void applyManualParameters (Runway runway, int length, int clearwayLength, int stopway, int displacedThreshold){
    this.setLength(length);
    this.clearwayLength = clearwayLength;
    this.stopway = stopway;
    this.displacedThreshold = displacedThreshold;

    this.getClearedAndGradedWidth();
    this.getClearedAndGradedLengthBeyondRunwayEnds();

    runway.redeclareALL();

  }

}
