package frc.team2767.deepspace.command.approach.sandstorm;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.PathCommand;
import frc.team2767.deepspace.command.approach.sequences.AutoHatchPickupCommandGroup;
import frc.team2767.deepspace.command.approach.sequences.AutoHatchPlaceCommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitPositionAboveCameraCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.subsystem.FieldDirection;

public class SandstormCommandGroup extends CommandGroup {

  public SandstormCommandGroup() {
    addSequential(new SetFieldDirectionCommand(FieldDirection.LEFT));
    addSequential(new PathCommand("hab_to_cargo_l", 90.0));
    addSequential(new AutoHatchPlaceCommandGroup(0.0));
    addSequential(new PathCommand("cargo_front_to_loading_l", 90.0));
    addSequential(new AutoHatchPickupCommandGroup());
    addSequential(new SetFieldDirectionCommand(FieldDirection.RIGHT));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new BiscuitPositionAboveCameraCommand());
            addParallel(new PathCommand("loading_to_cargo_side_l_0", 0.0));
          }
        });
    addSequential(new AutoHatchPlaceCommandGroup(-90.0)); // FIXME: left v. right
  }
}
