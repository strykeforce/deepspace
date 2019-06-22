package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.biscuit.ReleaseKrakenCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.log.SandstormHatchIndicator;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.command.vacuum.WaitForPressureCommand;
import frc.team2767.deepspace.subsystem.*;

public class SandstormHatchPickupCommandGroup extends CommandGroup {

  public SandstormHatchPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN SANDSTORM HATCH PICKUP"));
    addSequential(new SandstormHatchIndicator(false));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg), 0.05);
            addParallel(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.HATCH_PICKUP));
            addParallel(new SetActionCommand(Action.PLACE));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
            addParallel(new SetFieldDirectionCommand(FieldDirection.LEFT));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
            addParallel(new ReleaseKrakenCommand(false)); // Hatch staged with Kraken stowed
            addParallel(new IntakePositionCommand(IntakeSubsystem.kStowPositionDeg));
          }
        });
    addSequential(new ElevatorSetPositionCommand(7.5));
    addSequential(new WaitForPressureCommand(), 0.5);
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchLowPositionInches));
    addSequential(new SandstormHatchIndicator(true));
    addSequential(new LogCommand("END SANDSTORM HATCH PICKUP"));
  }
}
