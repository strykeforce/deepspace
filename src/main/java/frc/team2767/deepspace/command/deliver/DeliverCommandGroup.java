package frc.team2767.deepspace.command.deliver;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.twist.CalculateTwistCommand;
import frc.team2767.deepspace.command.twist.TwistCommand;
import frc.team2767.deepspace.command.vision.SelectCameraCommand;

public class DeliverCommandGroup extends CommandGroup {

  public DeliverCommandGroup() {
    addSequential(new SelectCameraCommand());
    addSequential(new QueryPyeyeCommand());
    addSequential(new CalculateTwistCommand());
    addSequential(new TwistCommand());
  }
}
