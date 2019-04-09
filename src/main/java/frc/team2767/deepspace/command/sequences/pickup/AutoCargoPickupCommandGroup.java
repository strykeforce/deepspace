package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.intake.WaitForBeamBreakCommand;

public class AutoCargoPickupCommandGroup extends CommandGroup {
  public AutoCargoPickupCommandGroup() {
    addSequential(new CargoGroundPickupCommandGroup());
    addSequential(new WaitForBeamBreakCommand());
    addSequential(new CoconutPickupCommandGroup());
  }
}
