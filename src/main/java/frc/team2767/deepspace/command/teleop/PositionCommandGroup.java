package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorPositionCommand;

public class PositionCommandGroup extends CommandGroup {

  public PositionCommandGroup() {
    addSequential(new ElevatorPositionCommand());
    addSequential(new BiscuitPositionCommand());
  }
}
