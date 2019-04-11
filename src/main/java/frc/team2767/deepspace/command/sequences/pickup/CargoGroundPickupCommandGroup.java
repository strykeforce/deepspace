package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.ReleaseKrakenCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerInCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class CargoGroundPickupCommandGroup extends CommandGroup {

  public CargoGroundPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN CARGO GROUND PICKUP"));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new ReleaseKrakenCommand(false));
            addParallel(new IntakePositionCommand(IntakeSubsystem.kLoadPositionDeg));
            addParallel(new RollerInCommand());
            addParallel(new HatchToBallConditionalCommand());
          }
        });
    //    addSequential(new ElevatorSetPositionCommand(22.25));
    addSequential(new LogCommand("END CARGO GROUND PICKUP"));
  }
}
