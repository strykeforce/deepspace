package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
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
    addSequential(new IntakePositionCommand(IntakeSubsystem.ShoulderPosition.UP));

    addSequential(new PressureSetCommand(VacuumSubsystem.VacuumPressure.HATCH));
    // vacuum
    addSequential(new WaitForPressureCommand(VacuumSubsystem.VacuumPressure.HATCH));

    addSequential(new LogCommand("opening valves"));
    addSequential(
        new ActivateValveCommand(
            new VacuumSubsystem.Valve[] {
              VacuumSubsystem.Valve.PUMP, VacuumSubsystem.Valve.TRIDENT
            }));
    addSequential(new LogCommand("opened valves"));

    addSequential(new SetActionCommand(Action.PLACE));
  }
}
