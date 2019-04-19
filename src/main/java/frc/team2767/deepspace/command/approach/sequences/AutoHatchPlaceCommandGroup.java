package frc.team2767.deepspace.command.approach.sequences;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.command.approach.*;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitPositionAboveCameraCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.subsystem.*;

public class AutoHatchPlaceCommandGroup extends CommandGroup {

  public AutoHatchPlaceCommandGroup() {
    addSequential(new LogCommand("BEGIN AUTO HATCH PLACE"));

    addSequential(
        new ConditionalCommand(new SetFieldDirectionCommand(FieldDirection.LEFT)) {
          @Override
          protected boolean condition() {
            return DriverStation.getInstance().isAutonomous();
          }
        });
    addSequential(new LightsOnCommand());
    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetActionCommand(Action.PLACE));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
          }
        });
    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchLowPositionInches));
    addSequential(new BiscuitPositionAboveCameraCommand());
    addSequential(new VisionAutoAlignPlaceCommand());
    addSequential(new BiscuitExecutePlanCommand());
    addSequential(new HoldHeadingUntilCompressionCommand());
    addSequential(new LogCommand("END HATCH PLACE"));
  }
}
