package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargoIntakeSubsystem extends Subsystem {

  private final int CLOSE_ENOUGH = 0; // FIXME
  private final int SHOULDER_ID = 20; // FIXME
  private final int ROLLER_ID = 21; // FIXME
  private final int SHOULDER_UP = 0; // FIXME
  private final int SHOULDER_LOAD = 0; // FIXME
  private final double ROLLER_SPEED = .2; // FIXME

  TalonSRX shoulder = new TalonSRX(SHOULDER_ID);
  TalonSRX roller = new TalonSRX(ROLLER_ID);

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  protected void initDefaultCommand() {}

  public void zero() {
    if (shoulder.getSensorCollection().isRevLimitSwitchClosed()) {
      shoulder.setSelectedSensorPosition(0, 0, 500);
    } else {
      logger.warn("Cargo intake zero failed");
    }
  }

  public int getShoulderPosition() {
    return shoulder.getSelectedSensorPosition();
  }

  int getEncoderValue(IntakePosition position) {
    switch (position) {
      case UP:
        return SHOULDER_UP;
      case LOAD:
        return SHOULDER_LOAD;
      default:
        logger.warn("Invalid cargoIntake position");
        return 0;
    }
  }

  public void setPosition(IntakePosition position) {
    shoulder.set(ControlMode.Position, getEncoderValue(position));
  }

  public void shoulderOpenLoopUp() {
    shoulder.set(ControlMode.PercentOutput, .20);
  }

  public void shoulderOpenLoopDown() {
    shoulder.set(ControlMode.PercentOutput, -.20);
  }

  public void shoulderStop() {
    shoulder.set(ControlMode.PercentOutput, 0);
  }

  public void rollerIn() {
    roller.set(ControlMode.PercentOutput, ROLLER_SPEED);
  }

  public void rollerOut() {
    roller.set(ControlMode.PercentOutput, -ROLLER_SPEED);
  }

  public void rollerStop() {
    roller.set(ControlMode.PercentOutput, 0);
  }

  public enum IntakePosition {
    UP,
    LOAD
  }
}
