package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
import frc.team2767.deepspace.command.vacuum.WaitForPressureCommand;
import frc.team2767.deepspace.subsystem.*;

public class PlayerHatchCommandGroup extends CommandGroup {

  public PlayerHatchCommandGroup() {
    addSequential(new SetFieldDirectionCommand(FieldDirection.LEFT));
    addSequential(new SetActionCommand(Action.PICKUP));
    addSequential(new SetGamePieceCommand(GamePiece.HATCH));
    addSequential(new SetLevelCommand(ElevatorLevel.ONE));
    addSequential(new PositionExecuteCommandGroup());

    // vacuum
    addSequential(new WaitForPressureCommand(VacuumSubsystem.VacuumPressure.CARGO));
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
