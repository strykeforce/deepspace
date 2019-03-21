package frc.team2767.deepspace.command.sequences.place;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.OrthogonalMovementCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.command.vision.SelectCameraCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class DeliverCommandGroup extends CommandGroup {

  public DeliverCommandGroup() {
    addSequential(new LogCommand("BEGIN DELIVER"));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
    addSequential(new SelectCameraCommand());
    addSequential(new QueryPyeyeCommand());
    addSequential(new OrthogonalMovementCommand());
    addSequential(new PositionExecuteCommandGroup());
    addSequential(new LogCommand("END DELIVER"));
  }
}
