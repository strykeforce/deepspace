package frc.team2767.deepspace.subsystem;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisionSubsystem extends Subsystem {

  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private final DigitalOutput lightsOutput = new DigitalOutput(0);

  private FieldDirection direction = FieldDirection.NOTSET;
  private ElevatorLevel elevatorLevel = ElevatorLevel.NOTSET;

  public VisionSubsystem() {
    lightsOutput.set(true);
  }

  public void enableLights(boolean state) {
    lightsOutput.set(!state);
  }

  public void setDirection(FieldDirection direction) {
    this.direction = direction;
    logger.debug("set direction to {}", direction);
  }

  public void setElevatorLevel(ElevatorLevel elevatorLevel) {
    this.elevatorLevel = elevatorLevel;
    logger.debug("set elevator level to {}", elevatorLevel);
  }

  @Override
  protected void initDefaultCommand() {}
}
