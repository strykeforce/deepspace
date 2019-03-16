package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSafePlaceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.subsystem.ElevatorLevel;

public class ElevatorLevelOneCommand extends CommandGroup {
  public ElevatorLevelOneCommand() {
    addSequential(new BiscuitSafePlaceCommand());
    addSequential(new SetLevelCommand(ElevatorLevel.ONE));
    addSequential(new ElevatorExecutePlanCommand());
  }
}
