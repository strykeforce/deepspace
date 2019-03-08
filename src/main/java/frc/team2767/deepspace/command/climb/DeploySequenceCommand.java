package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.log.LogCommand;

public class DeploySequenceCommand extends CommandGroup {
  public DeploySequenceCommand() {
    addSequential(new LogCommand("BEGIN DEPLOY SEQUENCE"));
    addSequential(new ReleaseClimbCommand());
    addSequential(new RaiseClimbCommand());
    addSequential(new LogCommand("END DEPLOY SEQUENCE"));
  }
}
