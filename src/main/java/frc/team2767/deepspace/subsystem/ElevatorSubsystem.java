package frc.team2767.deepspace.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.Disabled;
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class ElevatorSubsystem extends Subsystem implements Limitable {
  private static final int ID = 30;
  private static final int BACKUP = 2767;

  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private static final int TIMEOUT = 10;
  private static final int STABLE_THRESH = 1;
  public Position position;

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

  private final TalonSRX talon = new TalonSRX(ID);
  private final Preferences preferences;

  private boolean checkEncoder;
  private boolean upward;
  private boolean checkSlow;
  private boolean checkFast;
  private int startPosition;
  private int setpoint;
  private long positionStartTime;
  private int stableCount;

  @Override
  public int getPosition() {
    return 0;
  }

  @Override
  public void setLimits(int forward, int reverse) {}

  public ElevatorSubsystem() {
    this.preferences = Preferences.getInstance();

    if (talon == null) {
      logger.error("Talon not present");
    }

    elevatorPreferences();
    configTalon();
    logger.info("");
  }

  private void elevatorPreferences() {
    if (!preferences.containsKey(K_UP_ACCEL)) preferences.putInt(K_UP_ACCEL, BACKUP);
    if (!preferences.containsKey(K_UP_VELOCITY)) preferences.putInt(K_UP_VELOCITY, BACKUP);
    if (!preferences.containsKey(K_DOWN_SLOW_ACCEL)) preferences.putInt(K_DOWN_SLOW_ACCEL, BACKUP);
    if (!preferences.containsKey(K_DOWN_SLOW_VELOCITY))
      preferences.putInt(K_DOWN_SLOW_VELOCITY, BACKUP);
    if (!preferences.containsKey(K_DOWN_FAST_ACCEL)) preferences.putInt(K_DOWN_FAST_ACCEL, BACKUP);
    if (!preferences.containsKey(K_DOWN_FAST_VELOCITY))
      preferences.putInt(K_DOWN_FAST_VELOCITY, BACKUP);
    if (!preferences.containsKey(K_DOWN_VELOCITY_SHIFT_POS))
      preferences.putInt(K_DOWN_VELOCITY_SHIFT_POS, BACKUP);
    if (!preferences.containsKey(K_UP_OUTPUT)) preferences.putDouble(K_UP_OUTPUT, BACKUP);
    if (!preferences.containsKey(K_DOWN_OUTPUT)) preferences.putDouble(K_DOWN_OUTPUT, BACKUP);
    if (!preferences.containsKey(K_STOP_OUTPUT)) preferences.putDouble(K_STOP_OUTPUT, BACKUP);
    if (!preferences.containsKey(K_CLOSE_ENOUGH)) preferences.putInt(K_CLOSE_ENOUGH, BACKUP);

    // need to make the backups actually relevant
    kUpAccel = preferences.getInt(K_UP_ACCEL, 0);
    kUpVelocity = preferences.getInt(K_UP_VELOCITY, 0);
    kDownSlowAccel = preferences.getInt(K_DOWN_SLOW_ACCEL, 0);
    kDownSlowVelocity = preferences.getInt(K_DOWN_SLOW_VELOCITY, 0);
    kDownFastAccel = preferences.getInt(K_DOWN_FAST_ACCEL, 0);
    kDownFastVelocity = preferences.getInt(K_DOWN_FAST_VELOCITY, 0);
    kDownVelocityShiftPos = preferences.getInt(K_DOWN_VELOCITY_SHIFT_POS, 0);
    kUpOutput = preferences.getDouble(K_UP_OUTPUT, 0);
    kDownOutput = preferences.getDouble(K_DOWN_OUTPUT, 0);
    kStopOutput = preferences.getDouble(K_STOP_OUTPUT, 0);
    kCloseEnough = preferences.getInt(K_CLOSE_ENOUGH, 0);

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
    elevatorConfig.forwardSoftLimitEnable = true;
    elevatorConfig.forwardSoftLimitThreshold = 31_000;
    elevatorConfig.voltageCompSaturation = 12;
    elevatorConfig.voltageMeasurementFilter = 32;
    elevatorConfig.motionAcceleration = 2000;
    elevatorConfig.motionCruiseVelocity = 200;

    talon.configAllSettings(elevatorConfig);
    talon.enableCurrentLimit(true);
    talon.enableVoltageCompensation(true);

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(talon);
  }

  public void setPosition(Position position) {
    setpoint = position.position;
    startPosition = talon.getSelectedSensorPosition(0);
    logger.info("setting position = {}, starting at {}", position, startPosition);

    upward = setpoint > startPosition;

    if (upward) {
      talon.configMotionCruiseVelocity(kUpVelocity, 0);
      talon.configMotionAcceleration(kUpAccel, 0);
    } else {
      checkFast = checkSlow = true;
      adjustVelocity();
    }

    checkEncoder = true;
    positionStartTime = System.nanoTime();
    talon.set(ControlMode.MotionMagic, setpoint);
  }

  public void gamePieceAdjust(GamePiece currentGP, int height) {
    Position newPosition;

    if (currentGP.equals(GamePiece.CARGO)) {
      if (height == 0) newPosition = Position.CARGO_LOW;
      else if (height == 1) newPosition = Position.CARGO_MEDIUM;
      else newPosition = Position.CARGO_HIGH;
    } else if (currentGP.equals(GamePiece.HATCH)) {
      if (height == 0) newPosition = Position.HATCH_LOW;
      else if (height == 1) newPosition = Position.HATCH_MEDIUM;
      else newPosition = Position.HATCH_HIGH;
    } else {
      newPosition = Position.STOW;
    }

    setPosition(newPosition);
  }

  public void adjustVelocity() {
    int position = talon.getSelectedSensorPosition(0);

    if (checkEncoder) {
      long elapsed = System.nanoTime() - positionStartTime;
      if (elapsed < 200e6) return;

      if (Math.abs(position - startPosition) == 0) {
        talon.set(Disabled, 0);
        if (setpoint != 0) logger.error("no encoder movement detected in {} ms", elapsed / 1e6);
        setpoint = position;
        positionStartTime += elapsed;
        return;
      } else checkEncoder = false;
    }

    if (upward) return;

    if (checkFast && position > kDownVelocityShiftPos) {
      talon.configMotionCruiseVelocity(kDownFastVelocity, 0);
      talon.configMotionAcceleration(kDownFastAccel, 0);
      logger.debug("frontTalon velocity = fast ({}) position = {}", kDownFastVelocity, position);
      checkFast = false;
      return;
    }

    if (checkSlow && position < kDownVelocityShiftPos) {
      talon.configMotionCruiseVelocity(kDownSlowVelocity, 0);
      talon.configMotionAcceleration(kDownSlowAccel, 0);
      logger.debug("frontTalon velocity = slow ({}) position = {}", kDownSlowVelocity, position);
      checkFast = checkSlow = false;
    }
  }

  public boolean onTarget() {
    int error = setpoint - talon.getSelectedSensorPosition(0);
    if (Math.abs(error) > kCloseEnough) stableCount = 0;
    else stableCount++;
    if (stableCount > STABLE_THRESH) {
      logger.debug("stableCount > {}", STABLE_THRESH);
      return true;
    }
    return false;
  }

  public void positionToZero() {
    talon.configPeakCurrentLimit(2);
    talon.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

    logger.info("positioning to zero position");
    talon.set(PercentOutput, kDownOutput);
  }

  public boolean onZero() {
    return talon.getSensorCollection().isRevLimitSwitchClosed();
  }

  public void zeroPosition() {
    talon.selectProfileSlot(0, 0);
    setpoint = 0;
    ErrorCode err = talon.setSelectedSensorPosition(setpoint, 0, TIMEOUT);
    Errors.check(err, logger);
    // talon.set(ControlMode.MotionMagic, setpoint);
    logger.info("lift position zeroed, setpoint = {}", setpoint);

    talon.configPeakCurrentLimit(25);
    talon.enableCurrentLimit(true);
    talon.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
  }

  public void openLoopMove(Direction direction) {
    if (direction.equals(Direction.UP)) {
      logger.info("moving up at {}", kUpOutput);
      talon.set(ControlMode.PercentOutput, kUpOutput);
    } else if (direction.equals(Direction.DOWN)) {
      logger.info("moving down at {}", kDownOutput);
      talon.set(ControlMode.PercentOutput, kDownOutput);
    }
  }

  public void stop() {
    logger.info("lift stop at position {}", talon.getSelectedSensorPosition(0));
    talon.set(PercentOutput, kStopOutput);
  }

  @Override
  protected void initDefaultCommand() {}

  public enum Position {
    HATCH_LOW,
    HATCH_MEDIUM,
    HATCH_HIGH,
    STOW,
    CARGO_LOW,
    CARGO_MEDIUM,
    CARGO_HIGH;

    private static final String KEY_BASE = "ElevatorSubsystem/Position/";

    final int position;

    Position() {
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

  public enum GamePiece {
    HATCH,
    CARGO;
  }

  private final String PREFS_NAME = "ElevatorSubsystem/Settings/";
  private final String K_UP_ACCEL = PREFS_NAME + "u_accel";
  private final String K_UP_VELOCITY = PREFS_NAME + "u_velocity";
  private final String K_DOWN_SLOW_ACCEL = PREFS_NAME + "d_s_accel";
  private final String K_DOWN_SLOW_VELOCITY = PREFS_NAME + "d_s_velocity";
  private final String K_DOWN_FAST_ACCEL = PREFS_NAME + "d_f_accel";
  private final String K_DOWN_FAST_VELOCITY = PREFS_NAME + "d_f_velocity";
  private final String K_DOWN_VELOCITY_SHIFT_POS = PREFS_NAME + "d_velocity_shift_pos";
  private final String K_UP_OUTPUT = PREFS_NAME + "u_output";
  private final String K_DOWN_OUTPUT = PREFS_NAME + "d_output";
  private final String K_STOP_OUTPUT = PREFS_NAME + "stop_output";
  private final String K_CLOSE_ENOUGH = PREFS_NAME + "close_enough";
}
