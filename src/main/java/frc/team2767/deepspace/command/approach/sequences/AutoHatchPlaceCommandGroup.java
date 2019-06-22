package frc.team2767.deepspace.command.approach.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.FlipSandstormControlsCommand;
import frc.team2767.deepspace.command.approach.HoldHeadingUntilCompressionCommand;
import frc.team2767.deepspace.command.approach.SandstormSwapIfAutonConditionalCommand;
import frc.team2767.deepspace.command.approach.VisionAutoAlignPlaceCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitPositionAboveCameraCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.GamePiece;

public class AutoHatchPlaceCommandGroup extends CommandGroup {

  public AutoHatchPlaceCommandGroup(double gyroOffset) {

    addSequential(new LogCommand("BEGIN AUTO HATCH PLACE"));
    addSequential(new FlipSandstormControlsCommand(false));
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
    addSequential(new VisionAutoAlignPlaceCommand(gyroOffset));
    addSequential(new BiscuitExecutePlanCommand());
    addSequential(new HoldHeadingUntilCompressionCommand());
    addSequential(new SandstormSwapIfAutonConditionalCommand(true));
    addSequential(new LogCommand("END HATCH PLACE"));
  }
}
