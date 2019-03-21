package frc.team2767.deepspace.command.approach.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.CalculateRotationCommand;
import frc.team2767.deepspace.command.approach.OpenLoopDriveUntilCurrentCommand;
import frc.team2767.deepspace.command.approach.YawToTargetCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.GamePiece;

public class SandstormHatchPlaceCommandGroup extends CommandGroup {

  public SandstormHatchPlaceCommandGroup() {
    addSequential(
        new CommandGroup() {
          {
            addSequential(new SetFieldDirectionCommand(FieldDirection.LEFT));
            addParallel(new SetActionCommand(Action.PLACE));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
          }
        });

    addSequential(new LightsOnCommand());
    addSequential(new QueryPyeyeCommand());
    addSequential(new CalculateRotationCommand());
    addSequential(new YawToTargetCommand());
    addSequential(new BiscuitExecutePlanCommand());
    addSequential(new ElevatorExecutePlanCommand());
    addSequential(new OpenLoopDriveUntilCurrentCommand(), 5.0);
  }
}
