package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class ClimbLockCommand extends ConditionalCommand {

  public ClimbLockCommand() {
    super(new DeploySequenceCommand());
  }

  @Override
  protected boolean condition() {
    return !ClimbSubsystem.isReleased;
  }
}
