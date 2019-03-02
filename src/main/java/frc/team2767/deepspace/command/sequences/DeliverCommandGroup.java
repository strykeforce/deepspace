package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.twist.OrthogonalMovementCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.command.vision.SelectCameraCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class DeliverCommandGroup extends CommandGroup {

  public DeliverCommandGroup() {
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
    addSequential(new SelectCameraCommand());
    addSequential(new QueryPyeyeCommand());
    addSequential(new OrthogonalMovementCommand());
    addSequential(new PositionExecuteCommandGroup());
  }
}
