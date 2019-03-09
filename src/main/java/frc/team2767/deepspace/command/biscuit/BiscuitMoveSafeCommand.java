package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class BiscuitMoveSafeCommand extends ConditionalCommand {
  private static final VisionSubsystem VISION = Robot.VISION;

  public BiscuitMoveSafeCommand() {
    super(new BiscuitSetPositionCommand(0));
  }

  @Override
  protected boolean condition() {
    return (VISION.action == Action.PICKUP);
  }
}
