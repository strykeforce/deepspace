package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.biscuit.BiscuitZeroCommand;
import frc.team2767.deepspace.command.elevator.ElevatorZeroCommand;
import frc.team2767.deepspace.command.intake.IntakeZeroCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public final class ZeroAxisCommand extends CommandGroup {

  public ZeroAxisCommand() {}

  @Override
  protected void initialize() {
    addSequential(new ElevatorZeroCommand());
    addSequential(new BiscuitZeroCommand());
    addSequential(new IntakeZeroCommand());
  }
}
