package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitZeroCommand;
import frc.team2767.deepspace.command.elevator.ElevatorZeroCommand;
import frc.team2767.deepspace.command.intake.ShoulderZeroCommand;

public final class ZeroAxisCommand extends CommandGroup {

  public ZeroAxisCommand() {
    addSequential(new ElevatorZeroCommand());
    addSequential(new BiscuitZeroCommand());
    addSequential(new ShoulderZeroCommand());
  }
}
