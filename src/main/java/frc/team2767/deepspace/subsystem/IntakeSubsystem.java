package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeSubsystem extends Subsystem {

  private final int CLOSE_ENOUGH = 0; // FIXME
  private final int SHOULDER_ID = 20; // FIXME
  private final int ROLLER_ID = 21; // FIXME
  private final int SHOULDER_UP_POSITION = 0; // FIXME
  private final int SHOULDER_LOAD_POSITION = 0; // FIXME
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private TalonSRX shoulder = new TalonSRX(SHOULDER_ID);
  private TalonSRX roller = new TalonSRX(ROLLER_ID);

  @Override
  protected void initDefaultCommand() {}

  public void shoulderStop() {
    shoulder.set(ControlMode.PercentOutput, 0);
  }

  ////////////////////////////////////////////////////////////////////////////
  // SHOULDER
  ////////////////////////////////////////////////////////////////////////////

  /**
   * @param controlMode roller TalonSRX control mode
   * @param setpoint TalonSRX setpoint
   */
  public void runShoulderOutput(ControlMode controlMode, double setpoint) {
    roller.set(controlMode, setpoint);
  }

  // FIXME
  public int getShoulderPosition() {
    return shoulder.getSelectedSensorPosition();
  }

  // FIXME
  public void setPosition(ShoulderPosition position) {
    shoulder.set(ControlMode.Position, getEncoderValue(position));
  }

  // FIXME
  public int getEncoderValue(ShoulderPosition position) {
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

  // FIXME
  public void shoulderZero() {
    if (shoulder.getSensorCollection().isRevLimitSwitchClosed()) {
      shoulder.setSelectedSensorPosition(0, 0, 500);
    } else {
      logger.warn("Cargo intake shoulderZero failed");
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  // ROLLER
  ////////////////////////////////////////////////////////////////////////////

  /**
   * @param controlMode roller TalonSRX control mode
   * @param setpoint TalonSRX setpoint
   */
  public void setRollerOutput(ControlMode controlMode, double setpoint) {
    roller.set(controlMode, setpoint);
  }

  public void rollerStop() {
    roller.set(ControlMode.PercentOutput, 0);
  }

  public enum ShoulderPosition {
    UP,
    LOAD
  }
}
