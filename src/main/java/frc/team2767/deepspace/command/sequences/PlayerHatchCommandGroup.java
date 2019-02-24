package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.PressureAccumulateCommandGroup;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.WaitForPressureCommand;
import frc.team2767.deepspace.subsystem.*;

public class PlayerHatchCommandGroup extends CommandGroup {

  public PlayerHatchCommandGroup() {
    addSequential(new PressureAccumulateCommandGroup());
    addSequential(new SetFieldDirectionCommand(FieldDirection.LEFT));
    addSequential(new SetActionCommand(Action.PICKUP));
    addSequential(new SetGamePieceCommand(GamePiece.HATCH));
    addSequential(new SetLevelCommand(ElevatorLevel.ONE));
    addSequential(new PositionExecuteCommandGroup());
    addSequential(new IntakePositionCommand(IntakeSubsystem.kUpPosition));

    addSequential(new PressureSetCommand(VacuumSubsystem.kHatchPressure));
    // vacuum
    addSequential(new WaitForPressureCommand(VacuumSubsystem.kHatchPressure));

    addSequential(new SetActionCommand(Action.PLACE));
  }
}
