package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DeploySequenceCommand extends CommandGroup {
  public DeploySequenceCommand() {
    addSequential(new ReleaseClimbCommand());
    addSequential(new RaiseClimbCommand());
  }
}
