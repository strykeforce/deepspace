package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.GamePiece;

public class PlayerHatchCommandGroup extends CommandGroup {

  public PlayerHatchCommandGroup() {
    addSequential(new SetFieldDirectionCommand(FieldDirection.LEFT));
    addSequential(new SetActionCommand(Action.PICKUP));
    addSequential(new SetGamePieceCommand(GamePiece.HATCH));
    addSequential(new SetLevelCommand(ElevatorLevel.ONE));
    addSequential(new PositionExecuteCommandGroup());
  }
}
