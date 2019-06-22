package frc.team2767.deepspace.command.approach.sandstorm;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.StartSide;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class ChooseAutonFieldSideCommand extends ConditionalCommand {

  private static final VisionSubsystem VISION = Robot.VISION;

  public ChooseAutonFieldSideCommand(Command onLeft, Command onRight) {
    super(onLeft, onRight);
  }

  /** @return true if start side is LEFT */
  @Override
  protected boolean condition() {
    return VISION.startSide == StartSide.LEFT;
  }
}
