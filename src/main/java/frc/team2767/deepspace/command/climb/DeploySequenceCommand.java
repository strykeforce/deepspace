package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.sequences.StowAllCommandGroup;

public class DeploySequenceCommand extends CommandGroup {
  public DeploySequenceCommand() {
    addSequential(new LogCommand("BEGIN DEPLOY SEQUENCE"));
    addParallel(new StowAllCommandGroup());
    addSequential(new ReleaseClimbCommand());
    addSequential(new RaiseClimbCommand());
    addSequential(new LogCommand("END DEPLOY SEQUENCE"));
  }
}
