package frc.team2767.deepspace.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

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
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class ElevatorSubsystem extends Subsystem implements Limitable {
  private static final int ID = 30;
  private static final int BACKUP = 2767;

  private final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private final int TIMEOUT = 10;
  private final int STABLE_THRESH = 4;
  private final String PREFS_NAME = "ElevatorSubsystem/Settings/";
  private final TalonSRX elevator = new TalonSRX(ID);
  private final Preferences preferences;
  private ElevatorLevel elevatorLevel;
  private ElevatorPosition elevatorPosition;
  private GamePiece currentGamepiece;
  private int kUpAccel;
  private int kUpVelocity;
  private int kDownSlowAccel;
  private int kDownSlowVelocity;
  private int kDownFastAccel;
  private int kDownFastVelocity;
  private int kDownVelocityShiftPos;
  private double kUpOutput;
  private double kDownOutput;
  private double kStopOutput;
  private int kCloseEnough;
  private int kAbsoluteZero;
  private boolean checkEncoder = true;
  private boolean upward;
  private boolean checkSlow;
  private boolean checkFast;
  private int startPosition;
  private int setpoint;
  private long positionStartTime;
  private int stableCount;

  public ElevatorSubsystem() {
    this.preferences = Preferences.getInstance();

    if (elevator == null) {
      logger.error("Talon not present");
    }

    elevatorPreferences();
    configTalon();
    logger.info("");
  }

  private void elevatorPreferences() {
    kUpAccel = (int) getPreference("up_accel", 5000);
    kUpVelocity = (int) getPreference("up_vel", 1000);
    kDownSlowAccel = (int) getPreference("down_slow_accel", 2000);
    kDownSlowVelocity = (int) getPreference("down_fast_accel", 200);
    kDownFastAccel = (int) getPreference("down_fast_accel", 5000);
    kDownFastVelocity = (int) getPreference("down_fast_vel", 1000);
    kDownVelocityShiftPos = (int) getPreference("down_vel_shiftpos", 4000);
    kUpOutput = getPreference("up_output", 0.2);
    kDownOutput = getPreference("down_output", -0.2);
    logger.info("Elevator down output = {}", kDownOutput);
    kStopOutput = getPreference("absolute_zero", 0.0);
    kCloseEnough = (int) getPreference("close_enough", 100);
    kAbsoluteZero = (int) getPreference("absolute_zero", 1854);
    logger.info("Elevator absolut zero = {}", kAbsoluteZero);
  }

  private void configTalon() {
    TalonSRXConfiguration elevatorConfig = new TalonSRXConfiguration();
    elevatorConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;

    elevatorConfig.forwardSoftLimitEnable = true;
    elevatorConfig.reverseSoftLimitEnable = true;

    elevatorConfig.continuousCurrentLimit = 20;
    elevatorConfig.peakCurrentDuration = 40;
    elevatorConfig.peakCurrentLimit = 25;
    elevatorConfig.peakOutputForward = 1.0;
    elevatorConfig.peakOutputReverse = -1.0;
    elevatorConfig.slot0.kP = 1;
    elevatorConfig.slot0.kI = 0;
    elevatorConfig.slot0.kD = 40;
    elevatorConfig.slot0.kF = 0.25;
    elevatorConfig.slot0.integralZone = 0;
    elevatorConfig.velocityMeasurementWindow = 64;
    elevatorConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    elevatorConfig.slot0.allowableClosedloopError = 0;
    elevatorConfig.forwardSoftLimitThreshold = 32_000; // FIXME different for comp/proto
    elevatorConfig.voltageCompSaturation = 12;
    elevatorConfig.voltageMeasurementFilter = 32;
    elevatorConfig.motionAcceleration = 2000;
    elevatorConfig.motionCruiseVelocity = 1500;
    elevatorConfig.peakOutputReverse = -1.0;
    elevatorConfig.peakOutputForward = 1.0;

    elevator.configAllSettings(elevatorConfig);
    elevator.enableCurrentLimit(true);
    elevator.enableVoltageCompensation(true);

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonItem(elevator, "Elevator"));
  }

  @SuppressWarnings("Duplicates")
  private double getPreference(String name, double defaultValue) {
    String prefName = PREFS_NAME + name;
    Preferences preferences = Preferences.getInstance();
    if (!preferences.containsKey(prefName)) {
      preferences.putDouble(prefName, defaultValue);
    }
    double pref = preferences.getDouble(prefName, BACKUP);
    logger.info("{}={}", name, pref);
    return pref;
  }

  public void setElevatorLevel(ElevatorLevel elevatorLevel) {
    this.elevatorLevel = elevatorLevel;
  }

  @Override
  public int getTicks() {
    return elevator.getSelectedSensorPosition(0);
  }

  @Override
  public void setLimits(int forward, int reverse) {
    elevator.configForwardSoftLimitThreshold(forward, 0);
    elevator.configReverseSoftLimitThreshold(reverse, 0);
  }

  public void executePlan() {
    ElevatorPosition newPosition = ElevatorPosition.NOTSET;

    switch (currentGamepiece) {
      case HATCH:
        switch (elevatorLevel) {
          case ONE:
            newPosition = ElevatorPosition.HATCH_LOW;
            break;
          case TWO:
            newPosition = ElevatorPosition.HATCH_MEDIUM;
            break;
          case THREE:
            newPosition = ElevatorPosition.HATCH_HIGH;
            break;
          case NOTSET:
            logger.warn("level not set");
        }
        break;
      case CARGO:
        switch (elevatorLevel) {
          case ONE:
            newPosition = ElevatorPosition.CARGO_LOW;
            break;
          case TWO:
            newPosition = ElevatorPosition.CARGO_MEDIUM;
            break;
          case THREE:
            newPosition = ElevatorPosition.CARGO_HIGH;
            break;
          case NOTSET:
            logger.warn("level not set");
            break;
        }
        break;
      case NOTSET:
        logger.warn("no cargo set");
        break;
    }

    // stow?

    setElevatorPosition(newPosition);
  }

  public void setElevatorPosition(ElevatorPosition elevatorPosition) {
    setpoint = elevatorPosition.position;
    startPosition = elevator.getSelectedSensorPosition(0);
    logger.info(
        "setting elevatorPosition = {} ({}), starting at {}",
        setpoint,
        elevatorPosition,
        startPosition);

    upward = setpoint > startPosition;

    if (upward) {
      elevator.configMotionCruiseVelocity(kUpVelocity, 0);
      elevator.configMotionAcceleration(kUpAccel, 0);
    } else {
      checkFast = checkSlow = true;
      adjustVelocity();
    }

    stableCount = 0;
    checkEncoder = true;
    positionStartTime = System.nanoTime();
    elevator.set(ControlMode.MotionMagic, setpoint);
  }

  public void adjustVelocity() {
    int position = elevator.getSelectedSensorPosition(0);

    //    if (checkEncoder) {
    //      long elapsed = System.nanoTime() - positionStartTime;
    //      if (elapsed < 200e8) return;
    //
    //      if (Math.abs(position - startPosition) == 0) {
    //        elevator.set(Disabled, 0);
    //        logger.debug(
    //            "|position - startPosition| = |{} - {}| = {}",
    //            position,
    //            startPosition,
    //            Math.abs(position - startPosition));
    //
    //        if (setpoint != 0) {
    //          logger.error("no encoder movement detected in {} ms", elapsed / 1e6);
    //        }
    //        setpoint = position;
    //        positionStartTime += elapsed;
    //        return;
    //      } else checkEncoder = false;
    //    }

    if (upward) return;

    if (checkFast && position > kDownVelocityShiftPos) {
      elevator.configMotionCruiseVelocity(kDownFastVelocity, 0);
      elevator.configMotionAcceleration(kDownFastAccel, 0);
      logger.debug(
          "frontTalon velocity = fast ({}) elevatorPosition = {}", kDownFastVelocity, position);
      checkFast = false;
      return;
    }

    if (checkSlow && position < kDownVelocityShiftPos) {
      elevator.configMotionCruiseVelocity(kDownSlowVelocity, 0);
      elevator.configMotionAcceleration(kDownSlowAccel, 0);
      logger.debug(
          "frontTalon velocity = slow ({}) elevatorPosition = {}", kDownSlowVelocity, position);
      checkFast = checkSlow = false;
    }
  }

  public boolean onTarget() {
    int error = setpoint - elevator.getSelectedSensorPosition(0);
    if (Math.abs(error) > kCloseEnough) stableCount = 0;
    else stableCount++;
    if (stableCount > STABLE_THRESH) {
      logger.debug("stableCount > {}", STABLE_THRESH);
      return true;
    }
    return false;
  }

  public void safeZero() {
    if (elevator.getSensorCollection().isRevLimitSwitchClosed()) {
      logger.info("Preferences zero = {}", kAbsoluteZero);
      logger.info("Relative position = {}", elevator.getSelectedSensorPosition());
      logger.info(
          "Absolute position = {}", elevator.getSensorCollection().getPulseWidthPosition() & 0xFFF);

      int offset = elevator.getSensorCollection().getPulseWidthPosition() & 0xFFF - kAbsoluteZero;
      elevator.setSelectedSensorPosition(offset);
      logger.info("New relative position = {}", offset);
    } else {
      logger.error("Elevator zero failed - elevator not at bottom");
      elevator.configPeakOutputForward(0, 0);
      elevator.configPeakOutputReverse(0, 0);
    }
  }

  public void positionToZero() {
    elevator.configPeakCurrentLimit(2);
    elevator.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

    logger.info("positioning to zero elevatorPosition");
    elevator.set(PercentOutput, kDownOutput);
  }

  public boolean onZero() {
    return elevator.getSensorCollection().isRevLimitSwitchClosed();
  }

  public void zeroPosition() {
    elevator.selectProfileSlot(0, 0);
    int zero = elevator.getSensorCollection().getPulseWidthPosition() & 0xFFF - kAbsoluteZero;
    elevator.setSelectedSensorPosition(zero);

    // elevator.set(ControlMode.MotionMagic, setpoint);
    logger.info("lift elevatorPosition zeroed to = {}", zero);

    elevator.configPeakCurrentLimit(25);
    elevator.enableCurrentLimit(true);
    elevator.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
  }

  public void openLoopMove(Direction direction) {
    if (direction.equals(Direction.UP)) {
      logger.info("moving up at {}", kUpOutput);
      elevator.set(ControlMode.PercentOutput, kUpOutput);
    } else if (direction.equals(Direction.DOWN)) {
      logger.info("moving down at {}", kDownOutput);
      elevator.set(ControlMode.PercentOutput, kDownOutput);
    }
  }

  public void stop() {
    logger.info("lift stop at elevatorPosition {}", elevator.getSelectedSensorPosition(0));
    elevator.set(PercentOutput, 0);
  }

  public void setCurrentGamepiece(GamePiece currentGamepiece) {
    this.currentGamepiece = currentGamepiece;
  }

  @Override
  protected void initDefaultCommand() {}

  public enum ElevatorPosition {
    CARGO_PICKUP,
    HATCH_LOW,
    NOTSET,
    HATCH_MEDIUM,
    HATCH_HIGH,
    STOW,
    CARGO_LOW,
    CARGO_MEDIUM,
    CARGO_PLAYER, // 3268
    CARGO_HIGH;

    private static final String KEY_BASE = "ElevatorSubsystem/Position/";

    final int position;

    ElevatorPosition() {
      Preferences preferences = Preferences.getInstance();
      String key = KEY_BASE + this.name();
      if (!preferences.containsKey(key)) preferences.putInt(key, BACKUP);
      this.position = preferences.getInt(key, BACKUP);
    }
  }

  public enum Direction {
    UP,
    DOWN
  }
}
