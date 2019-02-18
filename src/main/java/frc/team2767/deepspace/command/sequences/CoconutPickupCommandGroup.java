package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.RollerStopCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.GamePiece;

public class CoconutPickupCommandGroup extends CommandGroup {

  public CoconutPickupCommandGroup() {
    addSequential(
        new CommandGroup() {
          {
            addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.BiscuitPosition.UP));
            addSequential(new SetActionCommand(Action.PLACE));
            addSequential(new SetGamePieceCommand(GamePiece.CARGO));
            //    addSequential(new
            // ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.STOW));
            addSequential(new RollerStopCommand());
            // addSequential(new CoconutCloseCommand()); FIXME: need to add coconut (Arjav?)
            addSequential(
                new ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.CARGO_MEDIUM));
            addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.BiscuitPosition.DOWN_R));
          }
        });

    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.CARGO_PICKUP));

    //    addParallel(new PressureSetCommand(VacuumSubsystem.VacuumPressure.CARGO));
    //    addSequential(new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.CARGO_MEDIUM));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.BiscuitPosition.UP));
    //        addSequential(new
    // ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.STOW));
  }
}
