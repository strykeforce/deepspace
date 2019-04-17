package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.ApproachDirectionCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitMoveSafeCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.sequences.place.PositionExecuteCommandGroup;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.subsystem.*;

public class PlayerHatchCommandGroup extends CommandGroup {

  public PlayerHatchCommandGroup() {
    addSequential(new LogCommand("BEGIN PLAYER HATCH PICKUP"));
    addSequential(new ApproachDirectionCommand());
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.GAME_PIECE_PICKUP));
    //    addParallel(new AlignToFieldPickupCommand());
    addParallel(new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg), 0.02);
    addParallel(new BiscuitMoveSafeCommand());
    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetActionCommand(Action.PICKUP));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
          }
        });

    addSequential(new PositionExecuteCommandGroup());
    addSequential(new SetActionCommand(Action.PLACE));
    addSequential(new IntakePositionCommand(IntakeSubsystem.kStowPositionDeg));
    addSequential(new LogCommand("END PLAYER HATCH PICKUP"));
  }
}
