package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.sequences.PositionExecuteCommandGroup;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import frc.team2767.deepspace.subsystem.GamePiece;

public class PlayerHatchAutonPickupCommandGroup extends CommandGroup {

  public PlayerHatchAutonPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN AUTON HATCH PICKUP"));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetActionCommand(Action.PICKUP));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
          }
        });
    addSequential(new LightsOnCommand());
    addSequential(new FieldSquarePickupAlignmentCommand());
    addSequential(new QueryPyeyeCommand());
    addSequential(new PositionExecuteCommandGroup());

    //    addSequential(new TwistCommand());

    
    addSequential(new LogCommand("END AUTON HATCH PICKUP"));
  }
}
