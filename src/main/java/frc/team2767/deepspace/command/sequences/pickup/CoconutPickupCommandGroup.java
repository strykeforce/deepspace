package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerStopCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.subsystem.*;

public class CoconutPickupCommandGroup extends CommandGroup {

  public CoconutPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN COCONUT PICKUP"));

    addParallel(new IntakePositionCommand(IntakeSubsystem.kMiddlePositionDeg));
    addSequential(
        new CommandGroup() {
          {
            // addParallel(new RollerInCommand(0.2));
            addParallel(new SetActionCommand(Action.PLACE));
            addParallel(new SetGamePieceCommand(GamePiece.CARGO));
            addParallel(
                new CommandGroup() {
                  {
                    addSequential(
                        new SetSolenoidStatesCommand(
                            VacuumSubsystem.SolenoidStates.CARGO_PICKUP));
                    addSequential(new PressureSetCommand(VacuumSubsystem.kBallPressureInHg), 0.5);
                  }
                });
            // 20.25
            addParallel(new ElevatorSetPositionCommand(21.25));
          }
        });

    /*addSequential(
    new CommandGroup() {
      {
        addParallel(
            new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.HATCH_PICKUP));
        addParallel(new ElevatorDownFastOpenLoopCommand());
      }
    });*/

    addParallel(new IntakePositionCommand(105));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kDownPosition));
    // addSequential(new ElevatorDownFastOpenLoopCommand());
    // addSequential(new WaitForPressureCommand());
    addSequential(new CoconutPickupAutoRetryCommand());
    addSequential(new ElevatorSetPositionCommand(25.0));
    addParallel(new SetActionCommand(Action.PLACE));
    addParallel(new RollerStopCommand());
    addSequential(new LogCommand("END COCONUT PICKUP"));
  }
}
