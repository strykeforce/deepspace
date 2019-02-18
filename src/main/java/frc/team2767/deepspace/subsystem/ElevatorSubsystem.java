package frc.team2767.deepspace.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.Disabled;
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

public class ElevatorSubsystem extends Subsystem implements Limitable {
  private static final int ID = 30;
  private static final int BACKUP = 2767;

  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private static final int TIMEOUT = 10;
  private static final int STABLE_THRESH = 1;
  private final TalonSRX elevator = new TalonSRX(ID);
  private final Preferences preferences;

  private ElevatorLevel elevatorLevel;
  private ElevatorPosition elevatorPosition;
  private GamePiece currentGamepiece;

  public int plannedLevel = 0;
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
  private boolean checkEncoder;
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

  public void setElevatorLevel(ElevatorLevel elevatorLevel) {
    this.elevatorLevel = elevatorLevel;
  }

  private void elevatorPreferences() {

    String PREFS_NAME = "ElevatorSubsystem/Settings/";
    String k_UP_ACCEL = PREFS_NAME + "up_accel";
    if (!preferences.containsKey(k_UP_ACCEL)) preferences.putInt(k_UP_ACCEL, 5000);

    String k_UP_VELOCITY = PREFS_NAME + "up_vel";
    if (!preferences.containsKey(k_UP_VELOCITY)) preferences.putInt(k_UP_VELOCITY, 1000);

    String k_DOWN_SLOW_ACCEL = PREFS_NAME + "down_slow_accel";
    if (!preferences.containsKey(k_DOWN_SLOW_ACCEL)) preferences.putInt(k_DOWN_SLOW_ACCEL, 2000);

    String k_DOWN_SLOW_VELOCITY = PREFS_NAME + "down_slow_vel";
    if (!preferences.containsKey(k_DOWN_SLOW_VELOCITY))
      preferences.putInt(k_DOWN_SLOW_VELOCITY, 200);

    String k_DOWN_FAST_ACCEL = PREFS_NAME + "down_fast_accel";
    if (!preferences.containsKey(k_DOWN_FAST_ACCEL)) preferences.putInt(k_DOWN_FAST_ACCEL, 5000);

    String k_DOWN_FAST_VELOCITY = PREFS_NAME + "down_fast_vel";
    if (!preferences.containsKey(k_DOWN_FAST_VELOCITY))
      preferences.putInt(k_DOWN_FAST_VELOCITY, 1000);

    String k_DOWN_VELOCITY_SHIFT_POS = PREFS_NAME + "down_vel_shiftpos";
    if (!preferences.containsKey(k_DOWN_VELOCITY_SHIFT_POS))
      preferences.putInt(k_DOWN_VELOCITY_SHIFT_POS, 4000);

    String k_UP_OUTPUT = PREFS_NAME + "up_output";
    if (!preferences.containsKey(k_UP_OUTPUT)) preferences.putDouble(k_UP_OUTPUT, 0.2);

    String k_DOWN_OUTPUT = PREFS_NAME + "down_output";
    if (!preferences.containsKey(k_DOWN_OUTPUT)) preferences.putDouble(k_DOWN_OUTPUT, -0.2);

    String k_STOP_OUTPUT = PREFS_NAME + "stop_output";
    if (!preferences.containsKey(k_STOP_OUTPUT)) preferences.putDouble(k_STOP_OUTPUT, 0.0);

    String k_CLOSE_ENOUGH = PREFS_NAME + "close_enough";
    if (!preferences.containsKey(k_CLOSE_ENOUGH)) preferences.putInt(k_CLOSE_ENOUGH, 10);

    kUpAccel = preferences.getInt(k_UP_ACCEL, BACKUP);
    kUpVelocity = preferences.getInt(k_UP_VELOCITY, BACKUP);

    kDownSlowAccel = preferences.getInt(k_DOWN_SLOW_ACCEL, BACKUP);
    kDownSlowVelocity = preferences.getInt(k_DOWN_SLOW_VELOCITY, BACKUP);
    kDownFastAccel = preferences.getInt(k_DOWN_FAST_ACCEL, BACKUP);
    kDownFastVelocity = preferences.getInt(k_DOWN_FAST_VELOCITY, BACKUP);
    kDownVelocityShiftPos = preferences.getInt(k_DOWN_VELOCITY_SHIFT_POS, BACKUP);
    kUpOutput = preferences.getDouble(k_UP_OUTPUT, BACKUP);
    kDownOutput = preferences.getDouble(k_DOWN_OUTPUT, BACKUP);
    kStopOutput = preferences.getDouble(k_STOP_OUTPUT, BACKUP);
    kCloseEnough = preferences.getInt(k_CLOSE_ENOUGH, BACKUP);

    logger.info("kUpAccel: {}", kUpAccel);
    logger.info("kUpVelocity: {}", kUpVelocity);
    logger.info("kDownSlowAccel: {}", kDownSlowAccel);
    logger.info("kDownSlowVelocity: {}", kDownSlowVelocity);
    logger.info("kDownFastAccel: {}", kDownFastAccel);
    logger.info("kDownFastVelocity: {}", kDownFastVelocity);
    logger.info("kDownVelocityShiftPos: {}", kDownVelocityShiftPos);
    logger.info("kUpOutput: {}", kUpOutput);
    logger.info("kDownOutput: {}", kDownOutput);
    logger.info("kStopOutput: {}", kStopOutput);
    logger.info("kCloseEnough: {}", kCloseEnough);
  }

  private void configTalon() {
    TalonSRXConfiguration elevatorConfig = new TalonSRXConfiguration();
    elevatorConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;

    elevatorConfig.forwardSoftLimitEnable = true;
    elevatorConfig.reverseSoftLimitEnable = true;

    elevatorConfig.continuousCurrentLimit = 20;
    elevatorConfig.peakCurrentDuration = 40;
    elevatorConfig.peakCurrentLimit = 25;
    elevatorConfig.slot0.kP = 1;
    elevatorConfig.slot0.kI = 0;
    elevatorConfig.slot0.kD = 40;
    elevatorConfig.slot0.kF = 0.25;
    elevatorConfig.slot0.integralZone = 0;
    elevatorConfig.velocityMeasurementWindow = 64;
    elevatorConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    elevatorConfig.slot0.allowableClosedloopError = 0;
    elevatorConfig.forwardSoftLimitThreshold = 31_000;
    elevatorConfig.voltageCompSaturation = 12;
    elevatorConfig.voltageMeasurementFilter = 32;
    elevatorConfig.motionAcceleration = 2000;
    elevatorConfig.motionCruiseVelocity = 200;

    elevator.configAllSettings(elevatorConfig);
    elevator.enableCurrentLimit(true);
    elevator.enableVoltageCompensation(true);

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(elevator);
  }

  @Override
  public int getPosition() {
    return elevator.getSelectedSensorPosition(0);
  }

  @Override
  public void setLimits(int forward, int reverse) {
    elevator.configForwardSoftLimitThreshold(forward, 0);
    elevator.configReverseSoftLimitThreshold(reverse, 0);
  }

  public void setElevatorPosition(ElevatorPosition elevatorPosition) {
    setpoint = elevatorPosition.position;
    startPosition = elevator.getSelectedSensorPosition(0);
    logger.info("setting elevatorPosition = {}, starting at {}", elevatorPosition, startPosition);

    upward = setpoint > startPosition;

    if (upward) {
      elevator.configMotionCruiseVelocity(kUpVelocity, 0);
      elevator.configMotionAcceleration(kUpAccel, 0);
    } else {
      checkFast = checkSlow = true;
      adjustVelocity();
    }

    checkEncoder = true;
    positionStartTime = System.nanoTime();
    elevator.set(ControlMode.MotionMagic, setpoint);
  }

  public void executePlan() {
    ElevatorPosition newElevatorPosition;

    if (currentGamepiece.equals(GamePiece.CARGO)) {
      if (plannedLevel == 0) newElevatorPosition = ElevatorPosition.CARGO_LOW;
      else if (plannedLevel == 1) newElevatorPosition = ElevatorPosition.CARGO_MEDIUM;
      else newElevatorPosition = ElevatorPosition.CARGO_HIGH;
    } else if (currentGamepiece.equals(GamePiece.HATCH)) {
      if (plannedLevel == 0) newElevatorPosition = ElevatorPosition.HATCH_LOW;
      else if (plannedLevel == 1) newElevatorPosition = ElevatorPosition.HATCH_MEDIUM;
      else newElevatorPosition = ElevatorPosition.HATCH_HIGH;
    } else {
      newElevatorPosition = ElevatorPosition.STOW;
    }

    setElevatorPosition(newElevatorPosition);
  }

  public void adjustVelocity() {
    int position = elevator.getSelectedSensorPosition(0);

    if (checkEncoder) {
      long elapsed = System.nanoTime() - positionStartTime;
      if (elapsed < 200e6) return;

      if (Math.abs(position - startPosition) == 0) {
        elevator.set(Disabled, 0);
        if (setpoint != 0) logger.error("no encoder movement detected in {} ms", elapsed / 1e6);
        setpoint = position;
        positionStartTime += elapsed;
        return;
      } else checkEncoder = false;
    }

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

  @SuppressWarnings("Duplicates")
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
    int absoluteZero = 46;
    int zero = elevator.getSensorCollection().getPulseWidthPosition() & 0xFFF - absoluteZero;
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
    elevator.set(PercentOutput, kStopOutput);
  }

  @Override
  protected void initDefaultCommand() {}

  public enum ElevatorPosition {
    CARGO_PICKUP, // FIXME: add elevatorPosition to preferences
    HATCH_LOW,
    HATCH_MEDIUM,
    HATCH_HIGH,
    STOW,
    CARGO_LOW,
    CARGO_MEDIUM,
    CARGO_HIGH;

    private static final String KEY_BASE = "ElevatorSubsystem/BiscuitPosition/"; // FIXME

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
    DOWN;
  }
}
