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
  private int kCloseEnough; // FIXME
  private double kDownOuput;
  private int shoulderUpPosition; // FIXME
  private int shoulderLoadPosition; // FIXME
  private int shoulderZeroPosition;
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
    shoulder.setSelectedSensorPosition(shoulderZeroPosition);
  }

  public void shoulderToZero() {
    shoulder.set(ControlMode.PercentOutput, kDownOuput);
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
        return shoulderUpPosition;
      case LOAD:
        return shoulderLoadPosition;
      default:
        logger.warn("Invalid intake position");
        return 0;
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  // ROLLER
  ////////////////////////////////////////////////////////////////////////////

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
