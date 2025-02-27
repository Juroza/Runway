package group50.model;

import static java.lang.Math.*;

public class Runway {

  public Runway(String name, int TORA, int TODA, int ASDA, int LDA, int DisplacedThreshold, int Clearway, int Stopway, int RESA) {
    this.name = name;
    this.TORA = TORA;
    this.TODA = TODA;
    this.ASDA = ASDA;
    this.LDA = LDA;
    this.DisplacedThreshold = DisplacedThreshold;
    this.Clearway = Clearway;
    this.Stopway = Stopway;
    this.RESA = max(240, RESA);

  }

  private String name;

  private int TORA; //take-off run available

  private int TODA; //take-off distance available

  private int ASDA; //accelerate-stop distance

  private int LDA; //landing distance available

  private int DisplacedThreshold; //can be used for take-off, but not landing

  private int Clearway;  //beyond TORA, can be used for plane to climb to a certain height

  private int Stopway;  //beyond TORA, can be used for an abandoned take-off

  private int RESA; //runway end safety area

  private int StripEnd; //distance between runway and stopway

  private int BlastProtection;  //safety distance behind aircraft

  private Surface ALS;  //approach landing surface

  private Surface TOCS; //take-off climb surface

  public String getName() {
    return name;
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
    return DisplacedThreshold;
  }

  public int getClarway() {
    return Clearway;
  }

  public int getStopway() {
    return Stopway;
  }

  public int getRESA() {
    return RESA;
  }

  public int getStripEnd() {
    return StripEnd;
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


}
