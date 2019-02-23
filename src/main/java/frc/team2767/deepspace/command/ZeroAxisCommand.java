package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitZeroCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSafeZeroCommand;
import frc.team2767.deepspace.command.intake.ShoulderZeroCommand;

public final class ZeroAxisCommand extends CommandGroup {

  public ZeroAxisCommand() {
    addSequential(new ElevatorSafeZeroCommand(), 5.0);
    addSequential(new BiscuitZeroCommand(), 5.0);
    addSequential(new ShoulderZeroCommand(), 5.0);
  }
}
