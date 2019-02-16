package frc.team2767.deepspace.subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisionSubsystem extends Subsystem {

  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private final DigitalOutput lightsOutput = new DigitalOutput(0);
  NetworkTableEntry bearingEntry;
  NetworkTableEntry rangeEntry;
  private NetworkTable table = NetworkTableInstance.getDefault().getTable("Pyeye");
  private FieldDirection direction = FieldDirection.NOTSET;
  private ElevatorLevel elevatorLevel = ElevatorLevel.NOTSET;

  private double rawRange;
  private double rawBearing;

  private double correctedRange;
  private double correctedHeading;

  private double targetYaw;

  private Camera camera;

  public VisionSubsystem() {
    lightsOutput.set(true);
  }

  public double getCorrectedRange() {
    return correctedRange;
  }

  public void setCorrectedRange(double correctedRange) {
    this.correctedRange = correctedRange;
  }

  public double getCorrectedHeading() {
    return correctedHeading;
  }

  public void setCorrectedHeading(double correctedBearing) {
    this.correctedHeading = correctedBearing;
  }

  public double getTargetYaw() {
    return targetYaw;
  }

  public void setTargetYaw(double targetYaw) {
    this.targetYaw = targetYaw;
  }

  public boolean isTargetAcquired() {
    if (rawRange > 0.0) {
      logger.debug("target found");
      return true;
    }
    return false;
  }

  public void setCamera(Camera camera) {
    logger.debug("chose {} camera", camera);
    this.camera = camera;
  }

  public Camera whichCamera() {
    return camera;
  }

  public void queryPyeye() {
    rangeEntry = table.getEntry("camera_bearing");
    bearingEntry = table.getEntry("camera_range");

    rawBearing = (double) bearingEntry.getNumber(0.0);
    rawRange = (double) rangeEntry.getNumber(-1.0);
  }

  public void enableLights(boolean state) {
    lightsOutput.set(!state);
  }

  public void setDirection(FieldDirection direction) {
    this.direction = direction;
    logger.debug("set direction to {}", direction);
  }

  public void setElevatorLevel(ElevatorLevel elevatorLevel) {
    this.elevatorLevel = elevatorLevel;
    logger.debug("set elevator level to {}", elevatorLevel);
  }

  public double getRawBearing() {
    return rawBearing;
  }

  public double getRawRange() {
    return rawRange;
  }

  @Override
  protected void initDefaultCommand() {}

  public enum Camera {
    LEFT,
    RIGHT,
    NOTSET
  }
}
