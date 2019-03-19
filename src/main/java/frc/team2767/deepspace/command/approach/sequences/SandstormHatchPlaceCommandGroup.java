package frc.team2767.deepspace.command.approach.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.CalculateRotationCommand;
import frc.team2767.deepspace.command.approach.YawToTargetCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.subsystem.*;

public class SandstormHatchPlaceCommandGroup extends CommandGroup {

  public SandstormHatchPlaceCommandGroup() {
    addSequential(
        new CommandGroup() {
          {
            addSequential(new SetFieldDirectionCommand(FieldDirection.LEFT));
            addParallel(new SetActionCommand(Action.PLACE));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
          }
        });

    addSequential(new LightsOnCommand());
    addSequential(new QueryPyeyeCommand());
    addSequential(new CalculateRotationCommand());
    addSequential(new YawToTargetCommand());
    //    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kLeftPositionDeg));
    //    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchLowPositionInches));
    //    addSequential(new OpenLoopDriveUntilCurrentCommand(), 5.0);
  }
}
