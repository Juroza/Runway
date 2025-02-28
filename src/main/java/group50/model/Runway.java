package group50.model;

public class Runway {

  public Runway(String name, int length, int stripLength, int stopway, int clearway, int displacedThreshold, int RESA) {
    this.name = name;
    this.length = length;
    this.stopway = stopway;
    this.clearway = clearway; //clearway should be greater than stopway
    this.TORA = length;
    this.ASDA = ASDA + stopway;
    this.TODA = length + clearway;

    this.displacedThreshold = displacedThreshold;
    this.LDA = length - displacedThreshold;

    this.stripLength = stripLength;
    this.stripEnd = stripLength - length;
    this.RESA = RESA;

    this.ALS = new Surface(); //assumes plane's angle of descent is 50:1
    this.TOCS = new Surface();  //assumes plane's angle of ascent is 50:1

  }

  private Obstacle obstacle = new Obstacle(0, 0);

  private Boolean hasObstacle() {
    return obstacle.getHeight() != 0;
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

  private int BlastProtection;  //safety distance behind aircraft

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
    return BlastProtection;
  }

  public Surface getALS() {
    return ALS;
  }

  public Surface getTOCS() {
    return TOCS;
  }

  public void setBlastProtection(int blastProtection) {
    this.BlastProtection = blastProtection;
  }


}
