package frc.team2767.deepspace.subsystem;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisionSubsystem extends Subsystem {

  private static final double CAMERA_X = 0.0;
  private static final double CAMERA_Y = -9.0;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final DigitalOutput lightsOutput6 = new DigitalOutput(6);
  private final DigitalOutput lightsOutput5 = new DigitalOutput(5);
  private final double CAMERA_POSITION_BEARING = -90.0;
  private final UsbCamera usbCamera;
  public GamePiece gamePiece = GamePiece.NOTSET;
  public Action action = Action.NOTSET;
  public FieldDirection direction = FieldDirection.NOTSET;
  public ElevatorLevel elevatorLevel = ElevatorLevel.NOTSET;
  private NetworkTable table;
  private NetworkTableEntry bearingEntry;
  private NetworkTableEntry rangeEntry;
  private NetworkTableEntry cameraIDEntry;
  private double rawRange;
  private double rawBearing;
  private double correctedRange;
  private double correctedHeading;

  public VisionSubsystem() {

    CameraServer cameraServer = CameraServer.getInstance();
    NetworkTableInstance instance = NetworkTableInstance.getDefault();
    table = instance.getTable("Pyeye");
    bearingEntry = table.getEntry("camera_bearing");
    rangeEntry = table.getEntry("camera_range");
    cameraIDEntry = table.getEntry("camera_id");

    usbCamera = cameraServer.startAutomaticCapture();
    logger.info("camera is connected = {}", usbCamera.isConnected());
    lightsOutput6.set(true);
    lightsOutput5.set(true);
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

  public double getCameraPositionBearing() {
    return CAMERA_POSITION_BEARING;
  }

  public double getCameraX() {
    return CAMERA_X;
  }

  public double getCameraY() {
    return CAMERA_Y;
  }

  public boolean isTargetAcquired() {
    if (rawRange > 0.0) {
      logger.info("target found");
      return true;
    }
    return false;
  }

  public void queryPyeye() {
    rawBearing = (double) bearingEntry.getNumber(0.0);
    rawRange = (double) rangeEntry.getNumber(-1.0);
  }

  public void enableLights(boolean enabled) {
    logger.info("lights to {}", !enabled);
    lightsOutput6.set(!enabled);
    lightsOutput5.set(!enabled);
  }

  public void setGamePiece(GamePiece gamePiece) {
    this.gamePiece = gamePiece;
    logger.info("set gamepiece to {}", gamePiece);
  }

  public void setAction(Action action) {
    this.action = action;
    logger.info("set action to {}", action);
  }

  public void setFieldDirection(FieldDirection direction) {
    this.direction = direction;
    selectCamera();
    logger.info("set direction to {}", direction);
  }

  public void selectCamera() {
    VisionSubsystem.Camera camera;
    camera = Camera.LEFT;
    if (direction == FieldDirection.RIGHT) {
      camera = Camera.RIGHT;
    }

    logger.info("chose {} camera", camera);
    cameraIDEntry.setNumber(camera.id);
  }

  public void setElevatorLevel(ElevatorLevel elevatorLevel) {
    this.elevatorLevel = elevatorLevel;
    logger.info("set elevator level to {}", elevatorLevel);
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
    LEFT(0),
    RIGHT(1);

    int id;

    Camera(int i) {
      id = i;
    }
  }
}
