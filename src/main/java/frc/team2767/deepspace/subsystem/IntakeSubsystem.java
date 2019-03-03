package frc.team2767.deepspace.subsystem;

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

public class IntakeSubsystem extends Subsystem implements Limitable, Zeroable {

  private static final double TICKS_PER_DEGREE = 90.0;
  private static final double TICKS_OFFSET = 9664.0;

  public static double kStowPositionDeg;
  public static double kZeroPositionTicks;
  public static double kMiddlePositionDeg;
  public static double kLoadPositionDeg;
  public static double kCargoPlayerPositionDeg;
  private static int kAbsoluteZero;
  private final int SHOULDER_ID = 20;
  private final int ROLLER_ID = 21;
  private final int STABLE_THRESH = 4;
  private final String PREFS_NAME = "IntakeSubsystem/Settings/";
  private final int BACKUP = 2767;
  private int kCloseEnoughTicks;
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private TalonSRX shoulder = new TalonSRX(SHOULDER_ID);

  private TalonSRX roller = new TalonSRX(ROLLER_ID);
  private int stableCount;
  private int setpointTicks;

  public IntakeSubsystem() {

    if (shoulder == null) {
      logger.error("Shoulder not present");
    }

    if (roller == null) {
      logger.error("Roller not present");
    }

    intakePreferences();
    configTalon();
    shoulder.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);
  }

  @SuppressWarnings("Duplicates")
  private void intakePreferences() {
    // ticks
    kZeroPositionTicks = getPreference("zero_position_ticks", 0);
    kCloseEnoughTicks = (int) getPreference("close_enough_ticks", 100);
    kAbsoluteZero = (int) getPreference("absolute_zero", 1658);

    // degrees
    kStowPositionDeg = getPreference("up_position_deg", 107);
    kMiddlePositionDeg = getPreference("middle_position_deg", 105);
    kCargoPlayerPositionDeg = getPreference("cargo_player_position_deg", 97.4);
    kLoadPositionDeg = getPreference("load_position_deg", 24.4);
  }

  @SuppressWarnings("Duplicates")
  private void configTalon() {
    TalonSRXConfiguration shoulderConfig = new TalonSRXConfiguration();
    shoulderConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    shoulderConfig.continuousCurrentLimit = 10;
    shoulderConfig.peakCurrentLimit = 15;
    shoulderConfig.peakCurrentDuration = 40;
    shoulderConfig.peakOutputForward = 1.0;
    shoulderConfig.peakOutputReverse = -1.0;
    shoulderConfig.slot0.kP = 8;
    shoulderConfig.slot0.kI = 0;
    shoulderConfig.slot0.kD = 0;
    shoulderConfig.slot0.kF = 1;
    shoulderConfig.slot0.integralZone = 0;
    shoulderConfig.slot0.allowableClosedloopError = 0;
    shoulderConfig.forwardSoftLimitEnable = true;
    shoulderConfig.reverseSoftLimitEnable = true;
    shoulderConfig.voltageCompSaturation = 12;
    shoulderConfig.voltageMeasurementFilter = 32;
    shoulderConfig.velocityMeasurementWindow = 64;
    shoulderConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    shoulderConfig.motionCruiseVelocity = 900;
    shoulderConfig.motionAcceleration = 3500;
    shoulderConfig.forwardSoftLimitThreshold = 9664;
    shoulderConfig.reverseSoftLimitThreshold = -250;

    TalonSRXConfiguration rollerConfig = new TalonSRXConfiguration();
    rollerConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    rollerConfig.continuousCurrentLimit = 10;
    rollerConfig.peakCurrentLimit = 0;
    rollerConfig.peakCurrentDuration = 0;

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
    telemetryService.register(new TalonItem(shoulder, "IntakeShoulder"));
    telemetryService.register(new TalonItem(roller, "IntakeRoller"));
  }

  public List<TalonSRX> getRollerTalon() {
    return List.of(roller);
  }

  public List<TalonSRX> getShoulderTalon() {
    return List.of(shoulder);
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

  public boolean zero() {
    boolean didZero = false;
    if (shoulder.getSensorCollection().isRevLimitSwitchClosed()) {
      int absPos = shoulder.getSensorCollection().getPulseWidthPosition() % 0xFFF;
      int offset = absPos - kAbsoluteZero;
      shoulder.setSelectedSensorPosition(offset + (int) kZeroPositionTicks);
      logger.debug("shoulder zeroed with limit switch to {}", offset + (int) kZeroPositionTicks);
      didZero = true;
    } else {
      logger.error("Intake zero failed - intake not in stowed position");
      shoulder.configPeakOutputForward(0, 0);
      shoulder.configPeakOutputReverse(0, 0);
    }

    shoulder.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);
    return didZero;
  }

  public void shoulderStop() {
    logger.debug("shoulder stop");
    shoulder.set(ControlMode.PercentOutput, 0.0);
  }

  @Override
  public int getTicks() {
    return shoulder.getSelectedSensorPosition();
  }

  @Override
  public void setLimits(int forward, int reverse) {
    shoulder.configForwardSoftLimitThreshold(forward, 0);
    shoulder.configReverseSoftLimitThreshold(reverse, 0);
  }

  public double getPosition() {
    double angle = (TICKS_OFFSET - shoulder.getSelectedSensorPosition()) / TICKS_PER_DEGREE;
    logger.debug("position in degrees = {}", angle);
    return angle;
  }

  public void setPosition(double angle) {
    setpointTicks = (int) (TICKS_OFFSET - TICKS_PER_DEGREE * angle);
    logger.debug("setting shoulder position={}", setpointTicks);
    shoulder.set(ControlMode.MotionMagic, setpointTicks);
  }

  @SuppressWarnings("Duplicates")
  public boolean onTarget() {
    int error = setpointTicks - shoulder.getSelectedSensorPosition(0);
    if (Math.abs(error) > kCloseEnoughTicks) stableCount = 0;
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
    logger.debug("rollers open loop at {}", setpoint);
    roller.set(ControlMode.PercentOutput, setpoint);
  }

  public void rollerStop() {
    logger.debug("roller stop");
    roller.set(ControlMode.PercentOutput, 0.0);
  }

  public void dump() {
    logger.info("intake position in degrees = {}", getPosition());
    logger.info("intake position in ticks = {}", getTicks());
  }
}
