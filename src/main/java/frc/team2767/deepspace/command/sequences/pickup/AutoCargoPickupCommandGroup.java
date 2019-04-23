package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.intake.WaitForBeamBreakCommand;
import frc.team2767.deepspace.command.intake.WaitForIntakeBeamCommand;

public class AutoCargoPickupCommandGroup extends CommandGroup {
  public AutoCargoPickupCommandGroup() {
    addParallel(new WaitForIntakeBeamCommand());
    addParallel(
        new CommandGroup() {
          {
            addSequential(new CargoGroundPickupCommandGroup());
            addSequential(new WaitForBeamBreakCommand());
            addSequential(new CoconutPickupCommandGroup());
          }
        });
  }
}
