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
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class IntakeSubsystem extends Subsystem implements Limitable {

  private final int SHOULDER_ID = 20;
  private final int ROLLER_ID = 21;
  private final int STABLE_THRESH = 4;
  private final String PREFS_NAME = "IntakeSubsystem/Settings/";
  private final int BACKUP = 2767;
  private int kCloseEnough;
  public static double kUpPosition;
  public static double kZeroPosition;
  public static double kMiddlePosition;
  public static double kLoadPosition;
  public static double kCargoPlayerPosition;

  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private TalonSRX shoulder = new TalonSRX(SHOULDER_ID);

  private TalonSRX roller = new TalonSRX(ROLLER_ID);
  private int stableCount;
  private int setpoint;

  private int kForwardLimit;
  private int kReverseLimit;

  public IntakeSubsystem() {

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
    kZeroPosition = getPreference("zero_position", -3.66);
    kLoadPosition = getPreference("load_position", 267.14);
    kMiddlePosition = getPreference("middle_position", 11);
    kUpPosition = getPreference("up_position", 0);
    kCargoPlayerPosition = getPreference("cargo_player_position", 59.85);
    kCloseEnough = (int) getPreference("close_enough", 20);
    kForwardLimit = (int) getPreference("shoulder_forward_soft_limit", 15100);
    kReverseLimit = (int) getPreference("shoulder_reverse_soft_limit", 0);
  }

  @SuppressWarnings("Duplicates")
  private void configTalon() {
    TalonSRXConfiguration shoulderConfig = new TalonSRXConfiguration();
    shoulderConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    shoulderConfig.continuousCurrentLimit = 5;
    shoulderConfig.peakCurrentDuration = 40;
    shoulderConfig.peakCurrentLimit = 10;
    shoulderConfig.peakOutputForward = 1.0;
    shoulderConfig.peakOutputReverse = -1.0;
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
    shoulderConfig.peakOutputForward = 1.0;
    shoulderConfig.peakOutputReverse = -1.0;

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

  public void shoulderZeroWithLimitSwitch() {
    if (shoulder.getSensorCollection().isRevLimitSwitchClosed()) {
      shoulder.setSelectedSensorPosition((int) kZeroPosition);
      logger.debug("shoulder zeroed with limit switch to {}", shoulder.getSelectedSensorPosition());
    } else {
      logger.error("Intake zero failed - intake not in stowed position");
      shoulder.configPeakOutputForward(0, 0);
      shoulder.configPeakOutputReverse(0, 0);
    }
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
    return -0.003376 * shoulder.getSelectedSensorPosition() + 109.03793;
  }

  public void setPosition(double angle) {
    setpoint = (int) (32301 - angle * -296);
    logger.debug("setting shoulder position={}", setpoint);
    shoulder.set(ControlMode.MotionMagic, setpoint);
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
    logger.debug("rollers open loop at {}", setpoint);
    roller.set(ControlMode.PercentOutput, setpoint);
  }

  public void rollerStop() {
    logger.debug("roller stop");
    roller.set(ControlMode.PercentOutput, 0.0);
  }
}
