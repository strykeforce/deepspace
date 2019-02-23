package frc.team2767.deepspace.subsystem;

import static frc.team2767.deepspace.subsystem.ElevatorLevel.NOTSET;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class BiscuitSubsystem extends Subsystem implements Limitable {

  private static final String PREFS = "BiscuitSubsystem/Position/";
  private static final int BACKUP = 2767;
  private final DriveSubsystem DRIVE = Robot.DRIVE;
  private final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final int BISCUIT_ID = 40;
  private final int TICKS_PER_REV = 12300;
  private final double TICKS_PER_DEGREE = 34.1;
  public static double kUp;
  public static double kLeft;
  public static double kRight;
  public static double kBackStopL;
  public static double kBackStopR;
  public static double kTiltUpL;
  public static double kTiltUpR;
  public static double kDown;
  private int kCloseEnough = 50; // FIXME
  private int kLowerLimit = -6170; // FIXME
  private int kUpperLimit = 6170; // FIXME
  private int kAbsoluteZero;
  private double targetBiscuitPosition = 0;
  private GamePiece currentGamePiece = GamePiece.NOTSET;
  private Action currentAction = Action.NOTSET;
  private ElevatorLevel targetLevel = NOTSET;
  private FieldDirection targetDirection = FieldDirection.NOTSET;
  private TalonSRX biscuit = new TalonSRX(BISCUIT_ID);

  public BiscuitSubsystem() {
    biscuitPreferences();
    configTalon();
    biscuit.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);
  }

  private void biscuitPreferences() {
    kAbsoluteZero = (int) getPreference("absolute_zero", 1413);
    kCloseEnough = (int) getPreference("close_enough", 50);
    kLowerLimit = (int) getPreference("lower_limit", -6170);
    kUpperLimit = (int) getPreference("upper_limit", 6170);

    kUp = getPreference("up", 0);
    kDown = getPreference("down_R", 180);
    kLeft = getPreference("left", -90);
    kRight = getPreference("right", 90);
    kBackStopL = getPreference("backstop_L", -135);
    kBackStopR = getPreference("backstop_R", 135);
    kTiltUpL = getPreference("tilt_up_L", -75);
    kTiltUpR = getPreference("tilt_up_R", 75);
  }

  private void configTalon() {
    TalonSRXConfiguration biscuitConfig = new TalonSRXConfiguration();
    biscuitConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    biscuitConfig.forwardSoftLimitThreshold = kUpperLimit;
    biscuitConfig.reverseSoftLimitThreshold = kLowerLimit;
    biscuitConfig.forwardSoftLimitEnable = true;
    biscuitConfig.reverseSoftLimitEnable = true;
    biscuitConfig.peakOutputForward = 1.0;
    biscuitConfig.peakOutputReverse = -1.0;

    biscuitConfig.slot0.kP = 1.0;
    biscuitConfig.slot0.kI = 0.0;
    biscuitConfig.slot0.kD = 0.0;
    biscuitConfig.slot0.kF = 0.65;

    // FIXME
    //    biscuitConfig.slot0.allowableClosedloopError = 0;

    biscuitConfig.slot0.integralZone = 0;
    biscuitConfig.peakCurrentDuration = 40;
    biscuitConfig.peakCurrentLimit = 25;
    biscuitConfig.continuousCurrentLimit = 20;
    biscuitConfig.peakOutputForward = 1.0;
    biscuitConfig.peakOutputReverse = -1.0;

    biscuitConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    biscuitConfig.velocityMeasurementWindow = 64;

    biscuitConfig.voltageCompSaturation = 12;
    biscuitConfig.voltageMeasurementFilter = 32;

    biscuitConfig.motionCruiseVelocity = 1_000;
    biscuitConfig.motionAcceleration = 2_000;

    biscuit.enableCurrentLimit(true);
    biscuit.enableVoltageCompensation(true);
    biscuit.configAllSettings(biscuitConfig);

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonItem(biscuit, "Biscuit"));
  }

  private double getPreference(String name, double defaultValue) {
    String prefName = PREFS + name;
    Preferences preferences = Preferences.getInstance();
    if (!preferences.containsKey(prefName)) {
      preferences.putDouble(prefName, defaultValue);
    }
    double pref = preferences.getDouble(prefName, BACKUP);
    logger.info("{}={}", name, pref);
    return pref;
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public String toString() {
    return "states="
        + "\n\t"
        + "current game piece = "
        + currentGamePiece.name()
        + "\n\t"
        + "target level = "
        + targetLevel.name()
        + "\n\t"
        + "target direction = "
        + targetDirection.name()
        + "\n\t"
        + "target position = "
        + targetBiscuitPosition;
  }

  @Override
  public int getTicks() {
    return biscuit.getSelectedSensorPosition() % TICKS_PER_REV;
  }

  @Override
  public void setLimits(int forward, int reverse) {
    biscuit.configForwardSoftLimitThreshold(forward, 0);
    biscuit.configReverseSoftLimitThreshold(reverse, 0);
  }

  public double getPosition() {
    int position = biscuit.getSelectedSensorPosition() % TICKS_PER_REV;
    return position / TICKS_PER_DEGREE;
  }

  public void setPosition(double angle) {
    if (angle == 180 && getPosition() < 0) {
      angle = -180;
    }
    int encoderPosition = (int) (angle * TICKS_PER_DEGREE);
    logger.info("biscuit setpoint = {} degrees at {}", angle, encoderPosition);
    biscuit.set(ControlMode.MotionMagic, encoderPosition);
  }

  public List getTalons() {
    return List.of(biscuit);
  }

  public void zero() {
    if (!biscuit.getSensorCollection().isFwdLimitSwitchClosed()) {
      int absPos = biscuit.getSensorCollection().getPulseWidthPosition() & 0xFFF;
      int relPos = biscuit.getSelectedSensorPosition();
      logger.info("Preferences zero = {}", kAbsoluteZero);
      logger.info("Relative position = {}", relPos);
      logger.info(
          "Absolute position = {}", absPos);

      int offset = kAbsoluteZero - absPos; //Absolute Encoder is out-of-phase with relative encoder
      biscuit.setSelectedSensorPosition(offset);
      logger.info("New relative position = {}", offset);
    } else {
      logger.error("Intake zero failed - biscuit not vertical");
      biscuit.configPeakOutputForward(0, 0);
      biscuit.configPeakOutputReverse(0, 0);
    }
    biscuit.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);
  }

  @SuppressWarnings("Duplicates")
  public void executePlan() {
    targetLevel = VISION.elevatorLevel;
    currentGamePiece = VISION.gamePiece;
    currentAction = VISION.action;
    targetDirection = VISION.direction;

    logger.debug(
        "plan running: level = {} gp = {} action = {}",
        targetLevel,
        currentGamePiece,
        currentAction);
    Angle currentAngle;
    double bearing = DRIVE.getGyro().getYaw();

    if (Math.abs(bearing) <= 90) {
      currentAngle = Angle.FORWARD;
    } else {
      currentAngle = Angle.BACKWARD;
    }

    switch (currentAction) {
      case PLACE:
        if (currentGamePiece == GamePiece.CARGO && targetLevel == ElevatorLevel.THREE) {
          switch (targetDirection) {
            case LEFT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = kTiltUpL;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = kTiltUpR;
                  break;
              }
              break;
            case RIGHT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = kTiltUpR;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = kTiltUpL;
                  break;
              }
          }
        } else {
          switch (targetDirection) {
            case LEFT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = kLeft;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = kRight;
                  break;
              }
              break;
            case RIGHT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = kRight;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = kLeft;
                  break;
              }
          }
        }
        break;

      case PICKUP:
        if (bearing <= 0 && bearing >= -180) {
          currentAngle = Angle.LEFT;
        } else {
          currentAngle = Angle.RIGHT;
        }
        switch (currentGamePiece) {
          case CARGO:
            switch (currentAngle) {
              case LEFT:
                targetBiscuitPosition = kBackStopR;
                break;
              case RIGHT:
                targetBiscuitPosition = kBackStopL;
                break;
            }
            break;
          case HATCH:
            switch (currentAngle) {
              case LEFT:
                targetBiscuitPosition = kLeft;
                break;
              case RIGHT:
                targetBiscuitPosition = kRight;
                break;
            }
        }
    }

    setPosition(targetBiscuitPosition);
  }

  public boolean onTarget() {
    if (Math.abs(biscuit.getSelectedSensorPosition() - targetBiscuitPosition) < kCloseEnough) {
      logger.debug(
          "current = {} target = {}", biscuit.getSelectedSensorPosition(), targetBiscuitPosition);
      logger.debug("on targetBiscuitPosition");
      return true;
    }

    return false;
  }

  public void runOpenLoop(double power) {
    biscuit.set(ControlMode.PercentOutput, power);
  }

  public void stop() {
    biscuit.set(ControlMode.PercentOutput, 0);
  }

  private enum Angle {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    NOTSET
  }
}
