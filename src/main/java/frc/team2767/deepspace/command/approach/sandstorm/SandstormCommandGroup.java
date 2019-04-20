package frc.team2767.deepspace.command.approach.sandstorm;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.PathCommand;

public class SandstormCommandGroup extends CommandGroup {

  public SandstormCommandGroup() {
    //    addSequential(new AutoHatchPlaceCommandGroup());
    //    addSequential(new PathCommand("cargo_side_to_loading_b_l", 90.0));
    //    addSequential(new AutoHatchPickupCommandGroup());
    addSequential(new PathCommand("loading_to_cargo_side_b_l", 0.0));
    //    addSequential(new AutoHatchPlaceCommandGroup());
  }
}
