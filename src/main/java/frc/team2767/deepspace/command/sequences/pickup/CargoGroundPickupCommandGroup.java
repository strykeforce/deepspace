package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerInCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class CargoGroundPickupCommandGroup extends CommandGroup {

  public CargoGroundPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN CARGO GROUND PICKUP"));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new ElevatorSetPositionCommand(22.0));
            addParallel(new IntakePositionCommand(IntakeSubsystem.kLoadPositionDeg));
            addParallel(new RollerInCommand());
            addParallel(new BiscuitSetPositionCommand(BiscuitSubsystem.kDownPosition));
          }
        });
    addSequential(new ElevatorSetPositionCommand(20.25));
    addSequential(new LogCommand("END CARGO GROUND PICKUP"));
  }
}
