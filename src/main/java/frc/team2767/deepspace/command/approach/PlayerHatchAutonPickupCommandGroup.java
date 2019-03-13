package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;

public class PlayerHatchAutonPickupCommandGroup extends CommandGroup {

  public PlayerHatchAutonPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN AUTON HATCH PICKUP"));
    addSequential(new LightsOnCommand());
    addSequential(new FieldSquareAlignmentCommand());
    addSequential(new QueryPyeyeCommand());
    //    addSequential(new TwistCommand());
    addSequential(new LogCommand("END AUTON HATCH PICKUP"));
  }
}
