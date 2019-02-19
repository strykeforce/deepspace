package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;

public class PositionExecuteCommandGroup extends CommandGroup {

  public PositionExecuteCommandGroup() {
    addSequential(new ElevatorExecutePlanCommand());
    addSequential(new BiscuitExecutePlanCommand());
  }
}
