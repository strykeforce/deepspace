package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.PressureAccumulateCommandGroup;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.subsystem.*;

public class PlayerHatchCommandGroup extends CommandGroup {

  public PlayerHatchCommandGroup() {
    addSequential(new PressureAccumulateCommandGroup());
    addParallel(new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetFieldDirectionCommand(FieldDirection.LEFT));
            addParallel(new SetActionCommand(Action.PICKUP));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
          }
        });

    addSequential(new PositionExecuteCommandGroup());
    addSequential(new SetActionCommand(Action.PLACE));
    addSequential(new IntakePositionCommand(IntakeSubsystem.kStowPositionDeg));
  }
}
