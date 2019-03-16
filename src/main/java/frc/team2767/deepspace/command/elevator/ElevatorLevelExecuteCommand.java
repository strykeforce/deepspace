package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.subsystem.ElevatorLevel;

public class ElevatorLevelExecuteCommand extends CommandGroup {
  public ElevatorLevelExecuteCommand(ElevatorLevel level) {
    addSequential(new SetLevelCommand(level));
    addSequential(new ElevatorExecutePlanCommand());
  }
}
