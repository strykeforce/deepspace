package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeSubsystem extends Subsystem implements Limitable {

  private final int SHOULDER_ID = 20;
  private final int ROLLER_ID = 21;
  private final int STABLE_THRESH = 4;
  private final String PREFS_NAME = "IntakeSubsytem/Settings/";
  private final String K_CLOSE_ENOUGH = PREFS_NAME + "close_enough";
  private final String SHOULDER_UP_POSITION = PREFS_NAME + "up_position";
  private final String SHOULDER_ZERO_POSITION = PREFS_NAME + "zero_position";
  private final String SHOULDER_LOAD_POSITION = PREFS_NAME + "load_position";
  private final String ROLLER_OUT_OUTPUT = PREFS_NAME + "roller_out";
  private final String ROLLER_IN_OPUTPUT = PREFS_NAME + "roller_in";
  private final String SHOULDER_UP_OUPTUT = PREFS_NAME + "shoulder_up";
  private final String SHOULDER_DOWN_OUPUT = PREFS_NAME + "shoulder_down";
  private final int BACKUP = 0;
  private int kCloseEnough;
  private int kShoulderUpPosition;
  private int kShoulderZeroPosition;
  private int kShoulderDownPosition;
  private int kShoulderLoadPosition;
  private double kRollerOut;
  private double kRollerIn;
  private double kShoulderUpOutput;
  private double kShoulderDownOutput;

  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private TalonSRX shoulder = new TalonSRX(SHOULDER_ID);

  private TalonSRX roller = new TalonSRX(ROLLER_ID);
  private int stableCount;
  private int setpoint;
  private int forwardShoulderSoftLimit; // FIXME
  private int reverseShoulderSoftLimit; // FIXME
  private Preferences preferences;

  public IntakeSubsystem() {
    this.preferences = Preferences.getInstance();

    if (shoulder == null) {
      logger.error("Shoulder not present");
    }

    if (roller == null) {
      logger.error("Roller not present");
    }

    shoulderPreferences();
    configTalon();
  }

  private void shoulderPreferences() {}

  @SuppressWarnings("Duplicates")
  private void configTalon() {
    // FIXME: wont't run
    TalonSRXConfiguration shoulderConfig = new TalonSRXConfiguration();
    shoulderConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    shoulderConfig.continuousCurrentLimit = 0;
    shoulderConfig.peakCurrentDuration = 0;
    shoulderConfig.peakCurrentLimit = 0;
    shoulderConfig.slot0.kP = 0;
    shoulderConfig.slot0.kI = 0;
    shoulderConfig.slot0.kD = 0;
    shoulderConfig.slot0.kF = 0;
    shoulderConfig.slot0.integralZone = 0;
    shoulderConfig.slot0.allowableClosedloopError = 0;
    shoulderConfig.forwardSoftLimitEnable = true;
    shoulderConfig.forwardSoftLimitThreshold = forwardShoulderSoftLimit;
    shoulderConfig.reverseSoftLimitEnable = true;
    shoulderConfig.reverseSoftLimitThreshold = reverseShoulderSoftLimit;
    shoulderConfig.voltageCompSaturation = 0;
    shoulderConfig.voltageMeasurementFilter = 0;
    shoulderConfig.motionAcceleration = 0;
    shoulderConfig.motionCruiseVelocity = 0;

    // FIXME: won't run
    TalonSRXConfiguration rollerConfig = new TalonSRXConfiguration();
    rollerConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    rollerConfig.continuousCurrentLimit = 0;
    rollerConfig.peakCurrentDuration = 0;
    rollerConfig.peakCurrentLimit = 0;
    rollerConfig.slot0.kP = 0;
    rollerConfig.slot0.kI = 0;
    rollerConfig.slot0.kD = 0;
    rollerConfig.slot0.kF = 0;
    rollerConfig.slot0.integralZone = 0;
    rollerConfig.slot0.allowableClosedloopError = 0;
    rollerConfig.forwardSoftLimitEnable = true;
    rollerConfig.forwardSoftLimitThreshold = forwardShoulderSoftLimit;
    rollerConfig.reverseSoftLimitEnable = true;
    rollerConfig.reverseSoftLimitThreshold = reverseShoulderSoftLimit;
    rollerConfig.voltageCompSaturation = 0;
    rollerConfig.voltageMeasurementFilter = 0;
    rollerConfig.motionAcceleration = 0;
    rollerConfig.motionCruiseVelocity = 0;

    shoulder.configAllSettings(shoulderConfig);
    shoulder.enableCurrentLimit(true);
    shoulder.enableVoltageCompensation(true);

    roller.configAllSettings(rollerConfig);
    roller.enableCurrentLimit(true);
    roller.enableVoltageCompensation(true);
  }

  @SuppressWarnings("Duplicates")
  private void elevatorPreferences() {
    if (!preferences.containsKey(SHOULDER_DOWN_OUPUT)) {
      preferences.putInt(SHOULDER_DOWN_OUPUT, BACKUP);
    }
    if (!preferences.containsKey(SHOULDER_UP_OUPTUT)) {
      preferences.putInt(SHOULDER_UP_OUPTUT, BACKUP);
    }
    if (!preferences.containsKey(SHOULDER_ZERO_POSITION)) {
      preferences.putInt(SHOULDER_ZERO_POSITION, BACKUP);
    }
    if (!preferences.containsKey(SHOULDER_LOAD_POSITION)) {
      preferences.putInt(SHOULDER_LOAD_POSITION, BACKUP);
    }
    if (!preferences.containsKey(SHOULDER_UP_POSITION)) {
      preferences.putInt(SHOULDER_UP_POSITION, BACKUP);
    }
    if (!preferences.containsKey(ROLLER_IN_OPUTPUT)) {
      preferences.putInt(ROLLER_IN_OPUTPUT, BACKUP);
    }
    if (!preferences.containsKey(ROLLER_OUT_OUTPUT)) {
      preferences.putInt(ROLLER_OUT_OUTPUT, BACKUP);
    }
    if (!preferences.containsKey(K_CLOSE_ENOUGH)) {
      preferences.putInt(K_CLOSE_ENOUGH, BACKUP);
    }

    // need to make the backups actually relevant
    kShoulderDownOutput = preferences.getInt(SHOULDER_DOWN_OUPUT, BACKUP);
    kShoulderUpOutput = preferences.getInt(SHOULDER_UP_OUPTUT, BACKUP);
    kShoulderZeroPosition = preferences.getInt(SHOULDER_ZERO_POSITION, BACKUP);
    kShoulderLoadPosition = preferences.getInt(SHOULDER_LOAD_POSITION, BACKUP);
    kShoulderUpPosition = preferences.getInt(SHOULDER_UP_POSITION, BACKUP);
    kRollerIn = preferences.getInt(ROLLER_IN_OPUTPUT, BACKUP);
    kRollerOut = preferences.getDouble(ROLLER_OUT_OUTPUT, BACKUP);
    kCloseEnough = preferences.getInt(K_CLOSE_ENOUGH, BACKUP);

    logger.info("kShoulderDownOutput: {}", kShoulderDownOutput);
    logger.info("kShoulderUpOutput: {}", kShoulderUpOutput);
    logger.info("kShoulderZeroPosition: {}", kShoulderZeroPosition);
    logger.info("kShoulderLoadPosition: {}", kShoulderLoadPosition);
    logger.info("kShoulderUpPosition: {}", kShoulderUpPosition);
    logger.info("kRollerIn: {}", kRollerIn);
    logger.info("kRollerOut: {}", kRollerOut);
    logger.info("kUpOutput: {}", kShoulderUpOutput);
    logger.info("kCloseEnough: {}", kCloseEnough);
  }

  ////////////////////////////////////////////////////////////////////////////
  // SHOULDER
  ////////////////////////////////////////////////////////////////////////////
  @Override
  protected void initDefaultCommand() {}

  /**
   * @param controlMode roller TalonSRX control mode
   * @param setpoint TalonSRX setpoint
   */
  public void shoulderOpenLoop(ControlMode controlMode, double setpoint) {
    roller.set(controlMode, setpoint);
  }

  public void shoulderZeroWithLimitSwitch() {
    shoulder.setSelectedSensorPosition(kShoulderZeroPosition);
  }

  public void shoulderToZero() {
    shoulder.set(ControlMode.PercentOutput, kShoulderDownOutput);
  }

  public boolean onZero() {
    return shoulder.getSensorCollection().isFwdLimitSwitchClosed();
  }

  public void shoulderStop() {
    shoulder.set(ControlMode.PercentOutput, 0.0);
  }

  @Override
  public int getPosition() {
    return shoulder.getSelectedSensorPosition();
  }

  @Override
  public void setLimits(int forward, int reverse) {
    shoulder.configForwardSoftLimitThreshold(forward);
    shoulder.configReverseSoftLimitThreshold(reverse);
  }

  public void setPosition(ShoulderPosition position) {
    setpoint = getPositionSetpoint(position);
    shoulder.set(ControlMode.Position, getPositionSetpoint(position));
  }

  private int getPositionSetpoint(ShoulderPosition position) {
    switch (position) {
      case UP:
        return kShoulderUpPosition;
      case LOAD:
        return kShoulderLoadPosition;
      default:
        logger.warn("Invalid intake position");
        return 0;
    }
  }

  public boolean onTarget() {
    int error = setpoint - shoulder.getSelectedSensorPosition(0);
    if (Math.abs(error) > kCloseEnough) stableCount = 0;
    else stableCount++;
    if (stableCount > STABLE_THRESH) {
      logger.debug("stableCount > {}", STABLE_THRESH);
      return true;
    }
    return false;
  }

  ////////////////////////////////////////////////////////////////////////////
  // ROLLER
  ////////////////////////////////////////////////////////////////////////////

  /** @param setpoint TalonSRX setpoint */
  public void rollerOpenLoop(double setpoint) {
    roller.set(ControlMode.PercentOutput, setpoint);
  }

  public void rollerStop() {
    roller.set(ControlMode.PercentOutput, 0.0);
  }

  public enum ShoulderPosition {
    UP,
    LOAD
  }
}