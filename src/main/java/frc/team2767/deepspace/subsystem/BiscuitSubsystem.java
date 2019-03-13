package frc.team2767.deepspace.subsystem;

import static frc.team2767.deepspace.subsystem.ElevatorLevel.NOTSET;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.health.Zeroable;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class BiscuitSubsystem extends Subsystem implements Limitable, Zeroable {

  private static final String PREFS = "BiscuitSubsystem/Position/";
  private static final int BACKUP = 2767;
  private static final int BISCUIT_ID = 40;
  private static final double TICKS_PER_DEGREE = 34.1;
  private static final double TICKS_OFFSET = 0;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  public static double kUpPositionDeg;
  public static double kLeftPositionDeg;
  public static double kRightPositionDeg;
  public static double kBackStopLeftPositionDeg;
  public static double kBackStopRightPositionDeg;
  public static double kTiltUpLeftPositionDeg;
  public static double kTiltUpRightPositionDeg;
  public static double kDownRightPositionDeg;
  public static double kDownLeftPositionDeg;
  private static int kCloseEnoughTicks;
  private static int kAbsoluteZeroTicks;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double targetBiscuitPositionDeg = 0;
  private GamePiece currentGamePiece = GamePiece.NOTSET; // FIXME: remove
  private ElevatorLevel targetLevel = NOTSET; // FIXME: remove
  private FieldDirection targetDirection = FieldDirection.NOTSET; // FIXME: remove
  private TalonSRX biscuit = new TalonSRX(BISCUIT_ID);
  private int setpointTicks;

  private int currentForwardLimit;
  private int currentReverseLimit;

  public BiscuitSubsystem() {

    currentForwardLimit = 0;
    currentReverseLimit = 0;

    biscuitPreferences();
    configTalon();
    biscuit.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);
  }

  private void biscuitPreferences() {
    // ticks
    kAbsoluteZeroTicks = (int) getPreference("absolute_zero_ticks", 2450);
    kCloseEnoughTicks = (int) getPreference("close_enough_ticks", 50);

    // degrees
    kUpPositionDeg = getPreference("up_deg", 0);
    kDownRightPositionDeg = getPreference("down_R_deg", 179);
    kDownLeftPositionDeg = getPreference("down_L_deg", -179);
    kLeftPositionDeg = getPreference("left_deg", -86.5);
    kRightPositionDeg = getPreference("right_deg", 87);
    kBackStopLeftPositionDeg = getPreference("backstop_L_deg", -135);
    kBackStopRightPositionDeg = getPreference("backstop_R_deg", 135);
    kTiltUpLeftPositionDeg = getPreference("tilt_up_L_deg", -65);
    kTiltUpRightPositionDeg = getPreference("tilt_up_R_deg", 64);
  }

  private void configTalon() {
    TalonSRXConfiguration biscuitConfig = new TalonSRXConfiguration();
    biscuitConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    biscuitConfig.forwardSoftLimitEnable = true;
    biscuitConfig.reverseSoftLimitEnable = true;
    biscuitConfig.peakOutputForward = 1.0;
    biscuitConfig.peakOutputReverse = -1.0;
    biscuitConfig.slot0.kP = 1.0;
    biscuitConfig.slot0.kI = 0.0;
    biscuitConfig.slot0.kD = 0.0;
    biscuitConfig.slot0.kF = 0.65;
    biscuitConfig.slot0.allowableClosedloopError = 0;
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
    telemetryService.register(new TalonItem(biscuit, "Biscuit"));
  }

  @SuppressWarnings("Duplicates")
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
        + targetBiscuitPositionDeg;
  }

  public List<TalonSRX> getTalons() {
    return List.of(biscuit);
  }

  public boolean zero() {
    boolean didZero = false;
    if (!biscuit.getSensorCollection().isFwdLimitSwitchClosed()) {
      int absPos = biscuit.getSensorCollection().getPulseWidthPosition() & 0xFFF;
      logger.info("Preferences zero = {} Absolute position = {}", kAbsoluteZeroTicks, absPos);

      // appears backwards because absolute and relative encoders are out-of-phase in hardware
      int offset = kAbsoluteZeroTicks - absPos;

      biscuit.setSelectedSensorPosition(offset);
      logger.info("New relative position = {}", offset);
      didZero = true;
    } else {
      logger.error("Intake zero failed - biscuit not vertical");
      biscuit.configPeakOutputForward(0, 0);
      biscuit.configPeakOutputReverse(0, 0);
    }

    biscuit.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

    return didZero;
  }

  @SuppressWarnings("Duplicates")
  public void executePlan() {
    targetLevel = VISION.elevatorLevel;
    currentGamePiece = VISION.gamePiece;
    Action currentAction = VISION.action;
    targetDirection = VISION.direction;

    logger.debug(
        "plan running: level = {} gp = {} action = {}",
        targetLevel,
        currentGamePiece,
        currentAction);
    double bearing = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360);

    switch (currentAction) {
      case PLACE:
        if (currentGamePiece == GamePiece.CARGO && targetLevel == ElevatorLevel.THREE) {
          switch (targetDirection) {
            case LEFT:
              targetBiscuitPositionDeg = kTiltUpLeftPositionDeg;
              break;
            case RIGHT:
              targetBiscuitPositionDeg = kTiltUpRightPositionDeg;
              break;
            case NOTSET:
              logger.warn("Direction not set");
              break;
          }
        } else {
          switch (targetDirection) {
            case LEFT:
              targetBiscuitPositionDeg = kLeftPositionDeg;
              break;
            case RIGHT:
              targetBiscuitPositionDeg = kRightPositionDeg;
              break;
            case NOTSET:
              logger.warn("Direction not set");
              break;
          }
        }
        break;

      case PICKUP:
        Angle currentAngle;
        if (bearing <= 0) {
          currentAngle = Angle.LEFT;
        } else {
          currentAngle = Angle.RIGHT;
        }
        switch (currentGamePiece) {
          case CARGO:
            switch (currentAngle) {
              case LEFT:
                targetBiscuitPositionDeg = kBackStopRightPositionDeg;
                break;
              case RIGHT:
                targetBiscuitPositionDeg = kBackStopLeftPositionDeg;
                break;
            }
            break;
          case HATCH:
            switch (currentAngle) {
              case LEFT:
                targetBiscuitPositionDeg = kLeftPositionDeg;
                break;
              case RIGHT:
                targetBiscuitPositionDeg = kRightPositionDeg;
                break;
            }
            break;
          case NOTSET:
            logger.warn("Gamepiece not set");
            break;
        }
        break;
      case NOTSET:
        logger.warn("Action not set");
        break;
    }

    setPosition(targetBiscuitPositionDeg);
  }

  public double getPosition() {
    return (TICKS_OFFSET - biscuit.getSelectedSensorPosition()) / TICKS_PER_DEGREE;
  }

  public void setPosition(double angle) {
    if (angle == kDownRightPositionDeg && getPosition() < 0) {
      angle = kDownLeftPositionDeg;
    }
    setpointTicks = (int) (TICKS_OFFSET - angle * TICKS_PER_DEGREE);
    logger.info("set position in degrees = {} in ticks = {}", angle, setpointTicks);
    biscuit.set(ControlMode.MotionMagic, setpointTicks);
  }

  public boolean onTarget() {
    int current = biscuit.getSelectedSensorPosition();
    if (Math.abs(current - setpointTicks) < kCloseEnoughTicks) {
      logger.info("onTarget: current = {} target = {}", current, setpointTicks);
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

  public void dump() {
    logger.info(
        "biscuit position in degrees = {} biscuit position in ticks = {}",
        getPosition(),
        getTicks());
  }

  @Override
  public int getTicks() {
    return biscuit.getSelectedSensorPosition();
  }

  @SuppressWarnings("Duplicates")
  @Override
  public void setLimits(int forward, int reverse) {
    if (forward != currentForwardLimit) {
      biscuit.configForwardSoftLimitThreshold(forward, 0);
      currentForwardLimit = forward;
    }

    if (reverse != currentReverseLimit) {
      biscuit.configReverseSoftLimitThreshold(reverse, 0);
      currentReverseLimit = reverse;
    }
  }

  private enum Angle {
    LEFT,
    RIGHT,
  }
}
