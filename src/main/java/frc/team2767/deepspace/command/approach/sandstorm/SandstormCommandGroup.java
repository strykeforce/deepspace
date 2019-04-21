package frc.team2767.deepspace.command.approach.sandstorm;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.ApproachDirectionCommand;
import frc.team2767.deepspace.command.approach.PathCommand;
import frc.team2767.deepspace.command.approach.sequences.AutoHatchPickupCommandGroup;
import frc.team2767.deepspace.command.approach.sequences.AutoHatchPlaceCommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitPositionAboveCameraCommand;
import frc.team2767.deepspace.command.sequences.pickup.SandstormHatchPickupCommandGroup;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.subsystem.FieldDirection;

public class SandstormCommandGroup extends CommandGroup {

  public SandstormCommandGroup() {
    addSequential(new SandstormHatchPickupCommandGroup(), 0.5);
    addSequential(new SetFieldDirectionCommand(FieldDirection.LEFT));
    addSequential(new LightsOnCommand());
    addSequential(
        new CommandGroup() {
          {
            addParallel(new PathCommand("hab_to_cargo_l", 90.0));
            addParallel(new BiscuitPositionAboveCameraCommand());
            addParallel(new LightsOnCommand());
          }
        });
    addSequential(new AutoHatchPlaceCommandGroup(0.0));
    addSequential(new ApproachDirectionCommand());
    addSequential(
        new CommandGroup() {
          {
            addParallel(new BiscuitPositionAboveCameraCommand());
            addParallel(new PathCommand("cargo_front_to_loading_l", 90.0));
            addParallel(new LightsOnCommand());
          }
        });
    addSequential(new AutoHatchPickupCommandGroup());
    addSequential(new SetFieldDirectionCommand(FieldDirection.RIGHT));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new BiscuitPositionAboveCameraCommand());
            addParallel(new PathCommand("loading_to_cargo_side_l_0", 0.0));
            addParallel(new LightsOnCommand());
          }
        });
    addSequential(new AutoHatchPlaceCommandGroup(-90.0)); // FIXME: left v. right
  }
}
