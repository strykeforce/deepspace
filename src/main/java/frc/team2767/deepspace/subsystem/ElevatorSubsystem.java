package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.sun.scenario.Settings;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;

import static com.ctre.phoenix.motorcontrol.ControlMode.Disabled;
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

public class ElevatorSubsystem extends Subsystem {
  private static final int ID = 30;

  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private static final int TIMEOUT = 10;
  private static final int STABLE_THRESH = 1;

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

  public ElevatorSubsystem() {
    this.preferences = Preferences.getInstance();

    if (talon == null) {
      logger.error("Talon not present");
    }

    //need to make the backups actually relevant
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
  }

  public void setPosition(int position) {
    setpoint = position;
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
    talon.set(ControlMode.MotionMagic, position);
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
    talon.set(ControlMode.MotionMagic, setpoint);
    logger.info("lift position zeroed, setpoint = {}", setpoint);
  }

  public void openLoopMove(Direction direction) {
    if (direction.equals(Direction.UP)) {
      talon.set(ControlMode.PercentOutput, kUpOutput);
    } else if (direction.equals(Direction.DOWN)) {
      talon.set(ControlMode.PercentOutput, kDownOutput);
    }
  }

  public void stop() {
    logger.info("lift stop at position {}", talon.getSelectedSensorPosition(0));
    talon.set(ControlMode.PercentOutput, kStopOutput);
  }

  @Override
  protected void initDefaultCommand() {}

  public enum Position {
    HATCH_LOW,
    HATCH_MEDIUM,
    HATCH_HIGH,
    CARGO_LOW,
    ROCKET_LOW,
    ROCKET_MEDIUM,
    ROCKET_HIGH;

    private static final String KEY_BASE = "ElevatorSubsystem/Position/";
    private static final int BACKUP = 2767;

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

  private final String PREFS_NAME = "Elevator";
  private final String K_UP_ACCEL = "$PREFS_NAME/u_accel";
  private final String K_UP_VELOCITY = "$PREFS_NAME/u_velocity";
  private final String K_DOWN_SLOW_ACCEL = "$PREFS_NAME/d_s_accel";
  private final String K_DOWN_SLOW_VELOCITY = "$PREFS_NAME/d_s_velocity";
  private final String K_DOWN_FAST_ACCEL = "$PREFS_NAME/d_f_accel";
  private final String K_DOWN_FAST_VELOCITY = "$PREFS_NAME/d_f_velocity";
  private final String K_DOWN_VELOCITY_SHIFT_POS = "$PREFS_NAME/d_velocity_shift_pos";
  private final String K_UP_OUTPUT = "$PREFS_NAME/u_output";
  private final String K_DOWN_OUTPUT = "$PREFS_NAME/d_output";
  private final String K_STOP_OUTPUT = "$PREFS_NAME/stop_output";
  private final String K_CLOSE_ENOUGH = "$PREFS_NAME/close_enough";
}


