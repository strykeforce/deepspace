package frc.team2767.deepspace.command.states;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.XboxControls;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetLevelTiltCommand extends ConditionalCommand {
  private static XboxControls controls;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private boolean triggerPressed;

  public SetLevelTiltCommand() {
    super(new SetLevelCommand(ElevatorLevel.THREE), new SetLevelCommand(ElevatorLevel.ONE));
  }

  @Override
  protected boolean condition() {
    controls = Robot.CONTROLS.getXboxControls();
    triggerPressed = controls.getLTrig() > 0;
    logger.info("Trigger pressed: {}", triggerPressed);
    return triggerPressed;
  }
}
