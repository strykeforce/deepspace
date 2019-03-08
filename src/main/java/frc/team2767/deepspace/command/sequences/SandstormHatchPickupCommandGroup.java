package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.log.SandstormHatchIndicator;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
import frc.team2767.deepspace.command.vacuum.PressureAccumulateCommandGroup;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.WaitForPressureCommand;
import frc.team2767.deepspace.subsystem.*;

public class SandstormHatchPickupCommandGroup extends CommandGroup {
  public SandstormHatchPickupCommandGroup() {
    addSequential(new SandstormHatchIndicator(false));
    addSequential(
        new CommandGroup() {
          {
            addSequential(new PressureAccumulateCommandGroup());
            addParallel(new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg), 0.5);
            addParallel(new SetActionCommand(Action.PLACE));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
            addParallel(new SetFieldDirectionCommand(FieldDirection.LEFT));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
          }
        });
    addSequential(new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    addSequential(new ElevatorSetPositionCommand(9.0));
    addSequential(new WaitForPressureCommand(VacuumSubsystem.kHatchPressureInHg));
    addSequential(new SandstormHatchIndicator(true));
    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchLowPositionInches));
  }
}
