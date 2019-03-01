package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerInCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class CargoGroundPickupCommandGroup extends CommandGroup {

  public CargoGroundPickupCommandGroup() {
    addSequential(new ElevatorSetPositionCommand(22.0));
    addSequential(new RollerInCommand());
    addSequential(
        new CommandGroup() {
          {
            addParallel(new BiscuitSetPositionCommand(BiscuitSubsystem.kDownRightPositionDeg));
            addParallel(new IntakePositionCommand(IntakeSubsystem.kLoadPositionDeg));
          }
        });
  }
}
