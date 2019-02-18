package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;

public class PositionCommandGroup extends CommandGroup {

  public PositionCommandGroup() {
    addSequential(new ElevatorExecutePlanCommand());
    addSequential(new BiscuitExecutePlanCommand());
  }
}
