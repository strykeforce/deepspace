package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;

public class SquareTwistCommandGroup extends CommandGroup {

  public SquareTwistCommandGroup() {
    addSequential(new LogCommand("BEGIN SQUARE AND TWIST"));
    addSequential(new LightsOnCommand());
    addSequential(new QueryPyeyeCommand());
    addSequential(new FieldSquarePickupAlignmentCommand());
    addSequential(new WaitCommand(0.5));
    addSequential(new QueryPyeyeCommand());
    addSequential(new CalculateTwistCommand(25.0));
    addSequential(new VisionTwistCommand());
    addSequential(new LogCommand("END SQUARE AND TWIST"));
  }
}
