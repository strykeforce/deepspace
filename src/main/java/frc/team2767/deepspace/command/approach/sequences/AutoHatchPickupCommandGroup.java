package frc.team2767.deepspace.command.approach.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.ApproachDirectionCommand;
import frc.team2767.deepspace.command.log.LogCommand;

public class AutoHatchPickupCommandGroup extends CommandGroup {

  public AutoHatchPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN AUTO HATCH PICKUP"));
    addSequential(new ApproachDirectionCommand());
    addSequential(new HatchPickupCommandGroup());
    addSequential(new LogCommand("END AUTO HATCH PICKUP"));
  }
}
