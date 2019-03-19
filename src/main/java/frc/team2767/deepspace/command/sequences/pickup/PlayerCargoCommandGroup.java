package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerInCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.subsystem.*;

public class PlayerCargoCommandGroup extends CommandGroup {

  public PlayerCargoCommandGroup() {
    addSequential(new LogCommand("BEGIN PLAYER CARGO PICKUP"));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetFieldDirectionCommand(FieldDirection.LEFT));
            addParallel(new SetActionCommand(Action.PICKUP));
            addParallel(new SetGamePieceCommand(GamePiece.CARGO));
          }
        });

    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoPlayerPositionInches));
    addParallel(new IntakePositionCommand(IntakeSubsystem.kCargoPlayerPositionDeg));
    addSequential(new BiscuitExecutePlanCommand());
    addParallel(new RollerInCommand(0.3));

    addSequential(new SetActionCommand(Action.PLACE));
    addSequential(new LogCommand("END PLAYER CARGO PICKUP"));
  }
}
