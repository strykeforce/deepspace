package frc.team2767.deepspace.subsystem;

import static frc.team2767.deepspace.subsystem.ElevatorLevel.NOTSET;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class BiscuitSubsystem extends Subsystem implements Limitable {

  private static final String KEY_BASE = "BiscuitSubsystem/Position/";
  private static final int BACKUP = 2767;
  private static Preferences preferences = Preferences.getInstance();
  private final DriveSubsystem DRIVE = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final int BISCUIT_ID = 40;
  private final int TICKS_PER_REV = 12300;
  private FieldDirection targetDirection = FieldDirection.NOTSET;
  private int zero = 0;
  private TalonSRX biscuit = new TalonSRX(BISCUIT_ID);
  private int CLOSE_ENOUGH = 50; // FIXME
  private int LOW_ENCODER_LIMIT = -6170; // FIXME
  private int HIGH_ENCODER_LIMIT = 6170; // FIXME
  private String absoluteZeroKey = KEY_BASE + "absolute_zero";
  private String lowLimitKey = KEY_BASE + "lower_limit";
  private String highLimitKey = KEY_BASE + "upper_limit";
  private String closeEnoughKey = KEY_BASE + "close_enough";
  private GamePiece currentGamePiece = GamePiece.NOTSET;
  private BiscuitPosition targetBiscuitPosition = BiscuitPosition.NOTSET;
  private Action currentAction = Action.NOTSET;
  private ElevatorLevel targetLevel = NOTSET;

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
        + targetBiscuitPosition.name();
  }

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
    targetBiscuitPosition = biscuitPosition;
    logger.info("biscuit setpoint = {} at {}", biscuitPosition, biscuitPosition.encoderPosition);
    biscuit.set(ControlMode.MotionMagic, biscuitPosition.encoderPosition);
  }

  public void zero() {
    if (!preferences.containsKey(absoluteZeroKey)) preferences.putInt(absoluteZeroKey, BACKUP);
    int absoluteZero = preferences.getInt(absoluteZeroKey, BACKUP);

    if (!biscuit.getSensorCollection().isFwdLimitSwitchClosed()) {
      logger.info("Preferences zero = {}", absoluteZero);
      logger.info("Relative position = {}", biscuit.getSelectedSensorPosition());
      logger.info(
          "Absolute position = {}", biscuit.getSensorCollection().getPulseWidthPosition() & 0xFFF);

      zero = biscuit.getSensorCollection().getPulseWidthPosition() & 0xFFF - absoluteZero;
      biscuit.setSelectedSensorPosition(zero);
      logger.info("New relative position = {}", zero);
    } else {
      logger.warn("Biscuit zero failed");
      biscuit.configPeakOutputForward(0, 0);
      biscuit.configPeakOutputReverse(0, 0);
    }
    biscuit.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);
  }

  public void setFieldDirection(FieldDirection direction) {
    logger.debug("setting field direction to {}", direction);
    targetDirection = direction;
  }

  public void manualPosition(BiscuitPosition biscuitPosition) {
    targetBiscuitPosition = biscuitPosition;
    setPosition(targetBiscuitPosition);
  }

  public void setCurrentGamePiece(GamePiece currentGamePiece) {
    logger.debug("setting game piece to = {}", currentGamePiece);
    this.currentGamePiece = currentGamePiece;
  }

  public void setCurrentAction(Action currentAction) {
    logger.debug("setting current action to = {}", currentAction);

    this.currentAction = currentAction;
  }

  public void setTargetLevel(ElevatorLevel targetLevel) {
    logger.debug("setting target level to = {}", targetLevel);
    this.targetLevel = targetLevel;
  }

  @SuppressWarnings("Duplicates")
  public void executePlan() {
    Angle currentAngle;
    double bearing = DRIVE.getGyro().getYaw();
    logger.debug("gyro angle = {}", DRIVE.getGyro().getYaw());

    if (Math.abs(bearing) <= 90) {
      currentAngle = Angle.FORWARD;
    } else {
      currentAngle = Angle.BACKWARD;
    }

    logger.debug("level = {} gp = {} action = {}", targetLevel, currentGamePiece, currentAction);
    switch (currentAction) {
      case PLACE:
        if (currentGamePiece == GamePiece.CARGO && targetLevel == ElevatorLevel.THREE) {
          logger.debug("tilting");
          switch (targetDirection) {
            case LEFT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = BiscuitPosition.TILT_UP_L;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = BiscuitPosition.TILT_UP_R;
                  break;
              }
              break;
            case RIGHT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = BiscuitPosition.TILT_UP_R;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = BiscuitPosition.TILT_UP_L;
                  break;
              }
          }
        } else {
          logger.debug("not tilting");
          switch (targetDirection) {
            case LEFT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = BiscuitPosition.LEFT;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = BiscuitPosition.RIGHT;
                  break;
              }
              break;
            case RIGHT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = BiscuitPosition.RIGHT;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = BiscuitPosition.LEFT;
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
        logger.debug("picking up");

        switch (currentGamePiece) {
          case CARGO:
            switch (currentAngle) {
              case LEFT:
                targetBiscuitPosition = BiscuitPosition.BACK_STOP_R;
                break;
              case RIGHT:
                targetBiscuitPosition = BiscuitPosition.BACK_STOP_L;
                break;
            }
            break;

          case HATCH:
            switch (currentAngle) {
              case LEFT:
                targetBiscuitPosition = BiscuitPosition.LEFT;
                break;
              case RIGHT:
                targetBiscuitPosition = BiscuitPosition.RIGHT;
                break;
            }
        }
    }

    setPosition(targetBiscuitPosition);
  }

  public boolean onTarget() {
    if (Math.abs(biscuit.getSelectedSensorPosition() - targetBiscuitPosition.encoderPosition)
        < CLOSE_ENOUGH) {
      logger.debug(
          "current = {} target = {}",
          biscuit.getSelectedSensorPosition(),
          targetBiscuitPosition.encoderPosition);
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
    DOWN,
    NOTSET;

    final int encoderPosition;

    BiscuitPosition() {
      String positionKey = KEY_BASE + this.name();
      if (!preferences.containsKey(positionKey)) preferences.putInt(positionKey, BACKUP);
      this.encoderPosition = preferences.getInt(positionKey, BACKUP);
    }
  }
}
