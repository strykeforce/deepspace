package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class IntakeSubsystem extends Subsystem implements Limitable {

  private final int SHOULDER_ID = 20;
  private final int ROLLER_ID = 21;
  private final int STABLE_THRESH = 4;
  private final String PREFS_NAME = "IntakeSubsystem/Settings/";
  private final String K_CLOSE_ENOUGH = PREFS_NAME + "close_enough";
  private final String SHOULDER_UP_POSITION = PREFS_NAME + "up_position";
  private final String SHOULDER_ZERO_POSITION = PREFS_NAME + "zero_position";
  private final String SHOULDER_LOAD_POSITION = PREFS_NAME + "load_position";
  private final String ROLLER_OUT_OUTPUT = PREFS_NAME + "roller_out_output";
  private final String ROLLER_IN_OUTPUT = PREFS_NAME + "roller_in_output";
  private final String SHOULDER_UP_OUTPUT = PREFS_NAME + "shoulder_up_output";
  private final String SHOULDER_DOWN_OUTPUT = PREFS_NAME + "shoulder_down_output";
  private final String K_FORWARD_SOFT_LIMIT = PREFS_NAME + "shoulder_forward_soft_limit";
  private final String K_REVERSE_SOFT_LIMIT = PREFS_NAME + "shoulder_reverse_soft_limit";
  private final int BACKUP = 2767;
  private int kCloseEnough;
  private int kShoulderUpPosition;
  private int kShoulderZeroPosition;
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

  private int kForwardLimit;
  private int kReverseLimit;

  private Preferences preferences;

  public IntakeSubsystem() {
    this.preferences = Preferences.getInstance();

    if (shoulder == null) {
      logger.error("Shoulder not present");
    }

    if (roller == null) {
      logger.error("Roller not present");
    }

    intakePreferences();
    configTalon();
  }

  @SuppressWarnings("Duplicates")
  private void intakePreferences() {
    if (!preferences.containsKey(SHOULDER_DOWN_OUTPUT)) {
      preferences.putDouble(SHOULDER_DOWN_OUTPUT, -0.2);
    }
    if (!preferences.containsKey(SHOULDER_UP_OUTPUT)) {
      preferences.putDouble(SHOULDER_UP_OUTPUT, 0.2);
    }
    if (!preferences.containsKey(SHOULDER_ZERO_POSITION)) {
      preferences.putInt(SHOULDER_ZERO_POSITION, -200);
    }
    if (!preferences.containsKey(SHOULDER_LOAD_POSITION)) {
      preferences.putInt(SHOULDER_LOAD_POSITION, 15100);
    }
    if (!preferences.containsKey(SHOULDER_UP_POSITION)) {
      preferences.putInt(SHOULDER_UP_POSITION, 0);
    }
    if (!preferences.containsKey(ROLLER_IN_OUTPUT)) {
      preferences.putDouble(ROLLER_IN_OUTPUT, 0.3);
    }
    if (!preferences.containsKey(ROLLER_OUT_OUTPUT)) {
      preferences.putDouble(ROLLER_OUT_OUTPUT, 0.3);
    }
    if (!preferences.containsKey(K_CLOSE_ENOUGH)) {
      preferences.putInt(K_CLOSE_ENOUGH, 0);
    }
    if (!preferences.containsKey(K_FORWARD_SOFT_LIMIT)) {
      preferences.putInt(K_FORWARD_SOFT_LIMIT, 15100);
    }
    if (!preferences.containsKey(K_REVERSE_SOFT_LIMIT)) {
      preferences.putInt(K_REVERSE_SOFT_LIMIT, 0);
    }

    kShoulderDownOutput = preferences.getDouble(SHOULDER_DOWN_OUTPUT, BACKUP);
    kShoulderUpOutput = preferences.getDouble(SHOULDER_UP_OUTPUT, BACKUP);
    kShoulderZeroPosition = preferences.getInt(SHOULDER_ZERO_POSITION, BACKUP);
    kShoulderLoadPosition = preferences.getInt(SHOULDER_LOAD_POSITION, BACKUP);
    kShoulderUpPosition = preferences.getInt(SHOULDER_UP_POSITION, BACKUP);
    kRollerIn = preferences.getDouble(ROLLER_IN_OUTPUT, BACKUP);
    kRollerOut = preferences.getDouble(ROLLER_OUT_OUTPUT, BACKUP);
    kCloseEnough = preferences.getInt(K_CLOSE_ENOUGH, BACKUP);
    kForwardLimit = preferences.getInt(K_FORWARD_SOFT_LIMIT, BACKUP);
    kReverseLimit = preferences.getInt(K_REVERSE_SOFT_LIMIT, BACKUP);

    logger.info("kShoulderDownOutput={}", kShoulderDownOutput);
    logger.info("kShoulderUpOutput={}", kShoulderUpOutput);
    logger.info("kShoulderZeroPosition={}", kShoulderZeroPosition);
    logger.info("kShoulderLoadPosition={}", kShoulderLoadPosition);
    logger.info("kShoulderUpPosition={}", kShoulderUpPosition);
    logger.info("kRollerIn={}", kRollerIn);
    logger.info("kRollerOut={}", kRollerOut);
    logger.info("kUpOutput={}", kShoulderUpOutput);
    logger.info("kCloseEnough={}", kCloseEnough);
    logger.info("kForwardLimit={}", kForwardLimit);
    logger.info("kReverseLimit={}", kReverseLimit);
  }

  @SuppressWarnings("Duplicates")
  private void configTalon() {
    TalonSRXConfiguration shoulderConfig = new TalonSRXConfiguration();
    shoulderConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    shoulderConfig.continuousCurrentLimit = 5;
    shoulderConfig.peakCurrentDuration = 40;
    shoulderConfig.peakCurrentLimit = 10;
    shoulderConfig.slot0.kP = 4;
    shoulderConfig.slot0.kI = 0;
    shoulderConfig.slot0.kD = 60;
    shoulderConfig.slot0.kF = 1;
    shoulderConfig.slot0.integralZone = 0;
    shoulderConfig.slot0.allowableClosedloopError = 0;
    shoulderConfig.forwardSoftLimitThreshold = kForwardLimit;
    shoulderConfig.reverseSoftLimitThreshold = kReverseLimit;
    shoulderConfig.forwardSoftLimitEnable = true;
    shoulderConfig.reverseSoftLimitEnable = true;
    shoulderConfig.voltageCompSaturation = 12;
    shoulderConfig.voltageMeasurementFilter = 32;
    shoulderConfig.motionAcceleration = 3000;
    shoulderConfig.motionCruiseVelocity = 1000;

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
    rollerConfig.voltageCompSaturation = 0;
    rollerConfig.voltageMeasurementFilter = 0;
    rollerConfig.motionAcceleration = 0;
    rollerConfig.motionCruiseVelocity = 0;

    shoulder.configForwardSoftLimitEnable(true);
    shoulder.configAllSettings(shoulderConfig);
    shoulder.enableCurrentLimit(true);
    shoulder.enableVoltageCompensation(true);
    logger.debug("configured shoulder talon");

    roller.configAllSettings(rollerConfig);
    roller.enableCurrentLimit(true);
    roller.enableVoltageCompensation(true);
    logger.debug("configured roller talon");

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(shoulder);
    telemetryService.register(roller);
  }

  @Override
  protected void initDefaultCommand() {}

  ////////////////////////////////////////////////////////////////////////////
  // SHOULDER
  ////////////////////////////////////////////////////////////////////////////

  /** @param setpoint TalonSRX setpoint */
  public void shoulderOpenLoop(double setpoint) {
    logger.debug("shoulder open loop at {}", setpoint);
    shoulder.set(ControlMode.PercentOutput, setpoint);
  }

  public void shoulderZeroWithLimitSwitch() {
    logger.debug("shoulder zeroing with limit switch");
    shoulder.setSelectedSensorPosition(kShoulderZeroPosition);
  }

  public void shoulderToZero() {
    logger.debug("running to zero");
    shoulder.set(ControlMode.PercentOutput, kShoulderDownOutput);
  }

  public boolean onZero() {
    if (shoulder.getSensorCollection().isRevLimitSwitchClosed()) {
      logger.debug("limit switch closed");
      return true;
    }

    return false;
  }

  public void shoulderStop() {
    logger.debug("shoulder stop");
    shoulder.set(ControlMode.PercentOutput, 0.0);
  }

  @Override
  public int getPosition() {
    return shoulder.getSelectedSensorPosition();
  }

  @Override
  public void setLimits(int forward, int reverse) {
    shoulder.configForwardSoftLimitThreshold(forward, 0);
    shoulder.configReverseSoftLimitThreshold(reverse, 0);
  }

  public void setPosition(ShoulderPosition position) {
    logger.debug("setting shoulder position={}", position);
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
        logger.warn("Invalid shoulder position");
        return 0;
    }
  }

  @SuppressWarnings("Duplicates")
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
    logger.debug("rollers open loop at ", setpoint);
    roller.set(ControlMode.PercentOutput, setpoint);
  }

  public void rollerStop() {
    logger.debug("roller stop");
    roller.set(ControlMode.PercentOutput, 0.0);
  }

  public enum ShoulderPosition {
    UP,
    LOAD
  }
}
