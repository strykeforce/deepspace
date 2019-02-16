package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class BiscuitSubsystem extends Subsystem implements Limitable {

  private static final String KEY_BASE = "BiscuitSubsystem/BiscuitPosition/";
  private static final int BACKUP = 2767;
  private static Preferences preferences = Preferences.getInstance();
  private final DriveSubsystem driveSubsystem = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final int BISCUIT_ID = 40;
  private final int TICKS_PER_REV = 12300;
  public FieldDirection plannedDirection;
  private int zero = 0;
  private TalonSRX biscuit = new TalonSRX(BISCUIT_ID);
  private int CLOSE_ENOUGH = 8; // FIXME
  private int LOW_ENCODER_LIMIT = -6170; // FIXME
  private int HIGH_ENCODER_LIMIT = 6170; // FIXME
  private String absoluteZeroKey = KEY_BASE + "absolute_zero";
  private String lowLimitKey = KEY_BASE + "lower_limit";
  private String highLimitKey = KEY_BASE + "upper_limit";
  private String closeEnoughKey = KEY_BASE + "close_enough";

  private BiscuitPosition plannedBiscuitPosition; // left or right
  private GamePiece currentGamePiece;
  private BiscuitPosition targetBiscuitPosition;
  private Action currentAction;
  private Level targetLevel;

  public BiscuitSubsystem() {
    biscuitPreferences();
    configTalon();
    biscuit.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);
  }

  public void biscuitPreferences() {
    if (!preferences.containsKey(closeEnoughKey)) preferences.putInt(closeEnoughKey, 50);
    if (!preferences.containsKey(absoluteZeroKey)) preferences.putInt(absoluteZeroKey, 1413);
    if (!preferences.containsKey(lowLimitKey)) preferences.putInt(lowLimitKey, -6170);
    if (!preferences.containsKey(highLimitKey)) preferences.putInt(highLimitKey, 6170);

    CLOSE_ENOUGH = preferences.getInt(closeEnoughKey, BACKUP);
    LOW_ENCODER_LIMIT = preferences.getInt(lowLimitKey, BACKUP);
    HIGH_ENCODER_LIMIT = preferences.getInt(highLimitKey, BACKUP);
  }

  private void configTalon() {
    TalonSRXConfiguration biscuitConfig = new TalonSRXConfiguration();
    biscuitConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    biscuitConfig.forwardSoftLimitThreshold = HIGH_ENCODER_LIMIT;
    biscuitConfig.reverseSoftLimitThreshold = LOW_ENCODER_LIMIT;
    biscuitConfig.forwardSoftLimitEnable = true;
    biscuitConfig.reverseSoftLimitEnable = true;

    biscuitConfig.slot0.kP = 1.0;
    biscuitConfig.slot0.kI = 0.0;
    biscuitConfig.slot0.kD = 0.0;
    biscuitConfig.slot0.kF = 0.65;

    //    biscuitConfig.slot0.allowableClosedloopError = 0;

    biscuitConfig.slot0.integralZone = 0;
    biscuitConfig.peakCurrentDuration = 40;
    biscuitConfig.peakCurrentLimit = 25;
    biscuitConfig.continuousCurrentLimit = 20;

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
    telemetryService.register(biscuit);
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public int getPosition() {
    return biscuit.getSelectedSensorPosition() % TICKS_PER_REV;
  }

  @Override
  public void setLimits(int forward, int reverse) {
    biscuit.configForwardSoftLimitThreshold(forward, 0);
    biscuit.configReverseSoftLimitThreshold(reverse, 0);
  }

  public void setPosition(BiscuitPosition biscuitPosition) {
    logger.info("biscuit setpoint = {}", biscuitPosition);
    if (biscuitPosition != null) {
      biscuit.set(ControlMode.MotionMagic, biscuitPosition.encoderPosition);
    }
  }

  public void zero() {
    if (!preferences.containsKey(absoluteZeroKey)) preferences.putInt(absoluteZeroKey, BACKUP);
    int absoluteZero = preferences.getInt(absoluteZeroKey, BACKUP);
    logger.info("Preferences zero = {}", absoluteZero);
    logger.info("Relative position = {}", biscuit.getSelectedSensorPosition());
    logger.info(
        "Absolute position = {}", biscuit.getSensorCollection().getPulseWidthPosition() & 0xFFF);

    zero = biscuit.getSensorCollection().getPulseWidthPosition() & 0xFFF - absoluteZero;
    biscuit.setSelectedSensorPosition(zero);
    logger.info("New relative position = {}", zero);
  }

  public void setPlan(FieldDirection direction, BiscuitPosition biscuitPosition) {
    plannedDirection = direction;
    plannedBiscuitPosition = biscuitPosition;
  }

  public void manualPosition(BiscuitPosition biscuitPosition) {
    targetBiscuitPosition = biscuitPosition;
    setPosition(targetBiscuitPosition);
  }

  public void executePlan() {
    double angle = getGyroAngle();

    switch (currentAction) {
      case PLACE:
        if (currentGamePiece == GamePiece.CARGO && targetLevel == Level.THREE) {
          switch (plannedDirection) {
            case LEFT:
            case RIGHT:
              // gyro logic
          }
        } else {
          switch (plannedDirection) {
            case LEFT:
            case RIGHT:
              // gyro logic

          }
        }

      case PICKUP:
        if (currentGamePiece == GamePiece.CARGO) {
          switch (plannedDirection) {
            case RIGHT:
            case LEFT:
          }

          // gyro logic
        } else {
          switch (plannedDirection) {
            case RIGHT:
            case LEFT:
              // gyro logic
          }
        }
    }

    //    switch (plannedBiscuitPosition) {
    //      case PLACE:
    //        if (plannedDirection == FieldDirection.RIGHT && Math.abs(angle) < 90
    //            || plannedDirection == FieldDirection.LEFT && Math.abs(angle) > 90) {
    //          targetBiscuitPosition = BiscuitPosition.RIGHT;
    //        } else {
    //          targetBiscuitPosition = BiscuitPosition.LEFT;
    //        }
    //        break;
    //      case LEVEL_3:
    //        if (plannedDirection == FieldDirection.RIGHT && Math.abs(angle) < 90
    //            || plannedDirection == FieldDirection.LEFT && Math.abs(angle) > 90) {
    //          if (currentGamePiece == GamePiece.CARGO)
    //            targetBiscuitPosition = BiscuitPosition.TILT_UP_R;
    //          if (currentGamePiece == GamePiece.HATCH) targetBiscuitPosition =
    // BiscuitPosition.RIGHT;
    //        } else {
    //          if (currentGamePiece == GamePiece.CARGO)
    //            targetBiscuitPosition = BiscuitPosition.TILT_UP_L;
    //          if (currentGamePiece == GamePiece.HATCH) targetBiscuitPosition =
    // BiscuitPosition.LEFT;
    //        }
    //        break;
    //      case PICKUP:
    //        if (angle > 0) {
    //          if (currentGamePiece == GamePiece.CARGO)
    //            targetBiscuitPosition = BiscuitPosition.BACK_STOP_L;
    //          if (currentGamePiece == GamePiece.HATCH) targetBiscuitPosition =
    // BiscuitPosition.RIGHT;
    //        } else {
    //          if (currentGamePiece == GamePiece.CARGO)
    //            targetBiscuitPosition = BiscuitPosition.BACK_STOP_R;
    //          if (currentGamePiece == GamePiece.HATCH) targetBiscuitPosition =
    // BiscuitPosition.LEFT;
    //        }
    //        break;
    //      case DOWN:
    //        if (biscuit.getSelectedSensorPosition() >= 0) {
    //          targetBiscuitPosition = BiscuitPosition.DOWN_L;
    //          logger.info("targetBiscuitPosition {}", targetBiscuitPosition);
    //          logger.info("position {}", biscuit.getSelectedSensorPosition());
    //        } else {
    //          targetBiscuitPosition = BiscuitPosition.DOWN_R;
    //        }
    //        break;
    //      default:
    //        targetBiscuitPosition = plannedBiscuitPosition;
    //        break;
    //    }
    setPosition(targetBiscuitPosition);
  }

  private double getGyroAngle() {
    AHRS gyro = driveSubsystem.getGyro();
    return (double) gyro.getYaw();
  }

  public boolean onTarget() {
    if (Math.abs(biscuit.getSelectedSensorPosition() - targetBiscuitPosition.encoderPosition)
        < CLOSE_ENOUGH) {
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

  public enum GamePiece {
    CARGO,
    HATCH
  }

  public enum Action {
    PICKUP,
    PLACE
  }

  public enum Level {
    ONE,
    TWO,
    THREE
  }

  public enum FieldDirection {
    LEFT,
    RIGHT
  }

  public enum BiscuitPosition {
    UP,
    DOWN_L,
    DOWN_R,
    LEFT,
    RIGHT,
    BACK_STOP_L,
    BACK_STOP_R,
    TILT_UP_L,
    TILT_UP_R,
    DOWN;

    final int encoderPosition;

    BiscuitPosition() {
      String positionKey = KEY_BASE + this.name();
      if (!preferences.containsKey(positionKey)) preferences.putInt(positionKey, BACKUP);
      this.encoderPosition = preferences.getInt(positionKey, BACKUP);
    }
  }
}
