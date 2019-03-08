package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.MoveToSafePositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;

public class PositionExecuteCommandGroup extends CommandGroup {

  public PositionExecuteCommandGroup() {
    addSequential(new LogCommand("BEGIN POSITION EXECUTE"));
    addSequential(new MoveToSafePositionCommand());
    addSequential(new ElevatorExecutePlanCommand());
    addSequential(new BiscuitExecutePlanCommand());
    addSequential(new LogCommand("END POSITION EXECUTE"));
  }
}
