package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorDownFastOpenLoopCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerOutCommand;
import frc.team2767.deepspace.command.intake.RollerStopCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.command.vacuum.WaitForPressureCommand;
import frc.team2767.deepspace.subsystem.*;

public class CoconutPickupCommandGroup extends CommandGroup {

  public CoconutPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN COCONUT PICKUP"));
    addSequential(new RollerOutCommand(0.2));
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE));
    addSequential(new PressureSetCommand(VacuumSubsystem.kBallPressureInHg), 0.5);
    addSequential(new LogCommand("opening valves"));
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.GAME_PIECE_PICKUP));
    addSequential(new LogCommand("opened valves"));

    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetActionCommand(Action.PLACE));
            addParallel(new SetGamePieceCommand(GamePiece.CARGO));
            addParallel(new IntakePositionCommand(IntakeSubsystem.kMiddlePositionDeg));
          }
        });

    addSequential(new ElevatorSetPositionCommand(22.0));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kDownPosition));

    addSequential(new ElevatorSetPositionCommand(17.8));
    addParallel(new IntakePositionCommand(105)); // 105

    addSequential(new ElevatorDownFastOpenLoopCommand());
    addSequential(new WaitForPressureCommand(VacuumSubsystem.kBallPressureInHg));
    addSequential(new ElevatorSetPositionCommand(25.0));
    addSequential(new SetActionCommand(Action.PLACE));
    addSequential(new RollerStopCommand());
    addSequential(new LogCommand("END COCONUT PICKUP"));
    //        addSequential(new
    // ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.kStowPositionDeg));
  }
}
