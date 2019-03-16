package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.log.LogCommand;

public class AutoHatchPickupCommandGroup extends CommandGroup {

  public AutoHatchPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN AUTO HATCH PICKUP"));
    //    addSequential(new SquareTwistCommandGroup());
    addSequential(new SnapDriveCommandGroup());
    addSequential(new LogCommand("END AUTO HATCH PICKUP"));
  }
}
