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

  private final DriveSubsystem driveSubsystem = Robot.DRIVE;
  private final TelemetryService telemetryService = Robot.TELEMETRY;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static Preferences preferences = Preferences.getInstance();
  private static final String KEY_BASE = "BiscuitSubsystem/Position/";
  private static final int BACKUP = 2767;

  private final int BISCUIT_ID = 40;
  private final int TICKS_PER_REV = 12300;
  public FieldDirections plannedDirection;
  private int zero = 0;
  private int target = 0;
  private TalonSRX biscuit = new TalonSRX(BISCUIT_ID);
  private int CLOSE_ENOUGH = 8; // FIXME
  private int LOW_ENCODER_LIMIT = -6170; // FIXME
  private int HIGH_ENCODER_LIMIT = 6170; // FIXME
  private String absoluteZeroKey = KEY_BASE + "absolute_zero";
  private String lowLimitKey = KEY_BASE + "lower_limit";
  private String highLimitKey = KEY_BASE + "upper_limit";
  private String closeEnoughKey = KEY_BASE + "close_enough";
  private Position plannedPosition = Position.UP;
  private BiscuitGamePiece gamePiece;

  public BiscuitSubsystem() {
    biscuitPreferences();

    biscuit.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

    telemetryService.register(biscuit);
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

    biscuit.configAllSettings(biscuitConfig);
  }

  public void biscuitPreferences() {
    if (!preferences.containsKey(closeEnoughKey)) preferences.putInt(closeEnoughKey, BACKUP);
    if (!preferences.containsKey(absoluteZeroKey)) preferences.putInt(absoluteZeroKey, BACKUP);
    if (!preferences.containsKey(lowLimitKey)) preferences.putInt(lowLimitKey, BACKUP);
    if (!preferences.containsKey(highLimitKey)) preferences.putInt(highLimitKey, BACKUP);

    CLOSE_ENOUGH = preferences.getInt(closeEnoughKey, BACKUP);
    LOW_ENCODER_LIMIT = preferences.getInt(lowLimitKey, BACKUP);
    HIGH_ENCODER_LIMIT = preferences.getInt(highLimitKey, BACKUP);
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

  public void setPosition() {
    double angle = getGyroAngle();
    switch (plannedPosition) {
      case PLACE:
        if (plannedDirection == FieldDirections.PLACE_R && Math.abs(angle) < 90
            || plannedDirection == FieldDirections.PLACE_L && Math.abs(angle) > 90) {
          target = Position.RIGHT.encoderPosition;
        } else {
          target = Position.LEFT.encoderPosition;
        }
        break;
      case LEVEL_3:
        if (plannedDirection == FieldDirections.PLACE_R && Math.abs(angle) < 90
            || plannedDirection == FieldDirections.PLACE_L && Math.abs(angle) > 90) {
          if (gamePiece == BiscuitGamePiece.CARGO) target = Position.TILT_UP_R.encoderPosition;
          if (gamePiece == BiscuitGamePiece.HATCH) target = Position.RIGHT.encoderPosition;
        } else {
          if (gamePiece == BiscuitGamePiece.CARGO) target = Position.TILT_UP_L.encoderPosition;
          if (gamePiece == BiscuitGamePiece.HATCH) target = Position.LEFT.encoderPosition;
        }
        break;
      case PICKUP:
        if (angle > 0) {
          if (gamePiece == BiscuitGamePiece.CARGO) target = Position.BACK_STOP_L.encoderPosition;
          if (gamePiece == BiscuitGamePiece.HATCH) target = Position.RIGHT.encoderPosition;
        } else {
          if (gamePiece == BiscuitGamePiece.CARGO) target = Position.BACK_STOP_R.encoderPosition;
          if (gamePiece == BiscuitGamePiece.HATCH) target = Position.LEFT.encoderPosition;
        }
        break;
      case DOWN:
        if (biscuit.getSelectedSensorPosition() >= 0) {
          target = Position.DOWN_L.encoderPosition;
          logger.info("target {}", target);
          logger.info("position {}", biscuit.getSelectedSensorPosition());
        } else {
          target = Position.DOWN_R.encoderPosition;
        }
        break;
      default:
        target = plannedPosition.encoderPosition;
        break;
    }
    biscuit.set(ControlMode.Position, target);
  }

  private double getGyroAngle() {
    AHRS gyro = driveSubsystem.getGyro();
    double angle = gyro.getYaw();
    return angle;
  }

  public boolean onTarget() {
    if (Math.abs(biscuit.getSelectedSensorPosition() - target) < CLOSE_ENOUGH) {
      return true;
    } else {
      return false;
    }
  }

  public void runOpenLoop(double power) {
    biscuit.set(ControlMode.PercentOutput, power);
  }

  public void stop() {
    biscuit.set(ControlMode.PercentOutput, 0);
  }

  public enum BiscuitGamePiece {
    CARGO,
    HATCH;
  }

  public enum Position {
    UP,
    DOWN_L,
    DOWN_R,
    LEFT,
    RIGHT,
    BACK_STOP_L,
    BACK_STOP_R,
    TILT_UP_L,
    TILT_UP_R,
    DOWN,
    PLACE,
    LEVEL_3,
    PICKUP;

    final int encoderPosition;

    Position() {
      String positionKey = KEY_BASE + this.name();
      if (!preferences.containsKey(positionKey)) preferences.putInt(positionKey, BACKUP);
      this.encoderPosition = preferences.getInt(positionKey, BACKUP);
    }
  }

  public enum FieldDirections {
    PLACE_L,
    PLACE_R,
  }
}
