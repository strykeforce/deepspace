package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeSubsystem extends Subsystem implements Limitable {

  private final int kCloseEnough = 0; // FIXME
  private final int SHOULDER_ID = 20; // FIXME
  private final int ROLLER_ID = 21; // FIXME
  private final int SHOULDER_UP_POSITION = 0; // FIXME
  private final int SHOULDER_LOAD_POSITION = 0; // FIXME
  private final int SHOULDER_ZERO_POSITION = 0;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private TalonSRX shoulder = new TalonSRX(SHOULDER_ID);
  private TalonSRX roller = new TalonSRX(ROLLER_ID);

  private final int STABLE_THRESH = 4;
  private int stableCount;
  private int setpoint;

  private final double kDownOuput = 0.1;

  private int forwardSoftLimit;
  private int reverseSoftLimit;

  @Override
  protected void initDefaultCommand() {}

  ////////////////////////////////////////////////////////////////////////////
  // SHOULDER
  ////////////////////////////////////////////////////////////////////////////

  /**
   * @param controlMode roller TalonSRX control mode
   * @param setpoint TalonSRX setpoint
   */
  public void shoulderOpenLoop(ControlMode controlMode, double setpoint) {
    roller.set(controlMode, setpoint);
  }

  public void shoulderZeroWithLimitSwitch() {
    shoulder.setSelectedSensorPosition(SHOULDER_ZERO_POSITION);
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
    forwardSoftLimit = forward;
    reverseSoftLimit = reverse;
  }

  public void setPosition(ShoulderPosition position) {
    setpoint = getPositionSetpoint(position);
    shoulder.set(ControlMode.Position, getPositionSetpoint(position));
  }

  private int getPositionSetpoint(ShoulderPosition position) {
    switch (position) {
      case UP:
        return SHOULDER_UP_POSITION;
      case LOAD:
        return SHOULDER_LOAD_POSITION;
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
}
