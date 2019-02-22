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

  private static final String PREFS = "BiscuitSubsystem/Position/";
  private static final int BACKUP = 2767;
  private final DriveSubsystem DRIVE = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final int BISCUIT_ID = 40;
  private final int TICKS_PER_REV = 12300;
  private final String ABSOLUTE_ZERO = PREFS + "absolute_zero";
  private final String LOW_LIMIT = PREFS + "lower_limit";
  private final String UPPER_LIMIT = PREFS + "upper_limit";
  private final String CLOSE_ENOUGH = PREFS + "close_enough";
  private int kCloseEnough = 50; // FIXME
  private int kLowerLimit = -6170; // FIXME
  private int kUpperLimit = 6170; // FIXME
  private int kAbsoluteZero;
  private int targetBiscuitPosition = BiscuitPosition.NOTSET.encoderPosition;
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

  public void biscuitPreferences() {
    kAbsoluteZero = (int) getPreference(ABSOLUTE_ZERO, 1413);
    kCloseEnough = (int) getPreference(CLOSE_ENOUGH, 50);
    kLowerLimit = (int) getPreference(LOW_LIMIT, -6170);
    kUpperLimit = (int) getPreference(UPPER_LIMIT, 6170);
  }

  private void configTalon() {
    TalonSRXConfiguration biscuitConfig = new TalonSRXConfiguration();
    biscuitConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    biscuitConfig.forwardSoftLimitThreshold = kUpperLimit;
    biscuitConfig.reverseSoftLimitThreshold = kLowerLimit;
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

  @SuppressWarnings("Duplicates")
  private double getPreference(String name, double defaultValue) {
    String prefName = PREFS + name;
    Preferences preferences = Preferences.getInstance();
    if (!preferences.containsKey(name)) {
      preferences.putDouble(prefName, defaultValue);
    }
    double pref = preferences.getDouble(name, BACKUP);
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
  public int getPosition() {
    return biscuit.getSelectedSensorPosition() % TICKS_PER_REV;
  }

  @Override
  public void setLimits(int forward, int reverse) {
    biscuit.configForwardSoftLimitThreshold(forward, 0);
    biscuit.configReverseSoftLimitThreshold(reverse, 0);
  }

  public void setPosition(BiscuitPosition biscuitPosition) {
    targetBiscuitPosition = biscuitPosition.encoderPosition;
    logger.info("biscuit setpoint = {} at {}", biscuitPosition, biscuitPosition.encoderPosition);
    biscuit.set(ControlMode.MotionMagic, biscuitPosition.encoderPosition);
  }

  public void setPosition(int biscuitPosition) {
    targetBiscuitPosition = biscuitPosition;
    logger.info("biscuit setpoint = {} at {}", biscuitPosition, biscuitPosition);
    biscuit.set(ControlMode.MotionMagic, biscuitPosition);
  }

  public void zero() {
    if (!biscuit.getSensorCollection().isFwdLimitSwitchClosed()) {
      logger.info("Preferences zero = {}", kAbsoluteZero);
      logger.info("Relative position = {}", biscuit.getSelectedSensorPosition());
      logger.info(
          "Absolute position = {}", biscuit.getSensorCollection().getPulseWidthPosition() & 0xFFF);

      int zero = biscuit.getSensorCollection().getPulseWidthPosition() & 0xFFF - kAbsoluteZero;
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
                  targetBiscuitPosition = BiscuitPosition.TILT_UP_L.encoderPosition;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = BiscuitPosition.TILT_UP_R.encoderPosition;
                  break;
              }
              break;
            case RIGHT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = BiscuitPosition.TILT_UP_R.encoderPosition;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = BiscuitPosition.TILT_UP_L.encoderPosition;
                  break;
              }
          }
        } else {
          switch (targetDirection) {
            case LEFT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = BiscuitPosition.LEFT.encoderPosition;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = BiscuitPosition.RIGHT.encoderPosition;
                  break;
              }
              break;
            case RIGHT:
              switch (currentAngle) {
                case FORWARD:
                  targetBiscuitPosition = BiscuitPosition.RIGHT.encoderPosition;
                  break;
                case BACKWARD:
                  targetBiscuitPosition = BiscuitPosition.LEFT.encoderPosition;
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
                targetBiscuitPosition = BiscuitPosition.BACK_STOP_R.encoderPosition;
                break;
              case RIGHT:
                targetBiscuitPosition = BiscuitPosition.BACK_STOP_L.encoderPosition;
                break;
            }
            break;
          case HATCH:
            switch (currentAngle) {
              case LEFT:
                targetBiscuitPosition = BiscuitPosition.LEFT.encoderPosition;
                break;
              case RIGHT:
                targetBiscuitPosition = BiscuitPosition.RIGHT.encoderPosition;
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
      Preferences preferences = Preferences.getInstance();
      String positionKey = PREFS + this.name();
      if (!preferences.containsKey(positionKey)) preferences.putInt(positionKey, BACKUP);
      this.encoderPosition = preferences.getInt(positionKey, BACKUP);
    }
  }
}
