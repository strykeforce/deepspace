package frc.team2767.deepspace.command.deliver;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorPositionCommand;
import frc.team2767.deepspace.command.intake.RollerStopCommand;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.subsystem.*;

public class CoconutPickupCommandGroup extends CommandGroup {

  public CoconutPickupCommandGroup() {
    addSequential(new SetActionCommand(Action.PLACE));
    addSequential(new SetGamePieceCommand(GamePiece.CARGO));
    addSequential(new ElevatorPositionCommand(ElevatorSubsystem.Position.STOW));
    addSequential(new RollerStopCommand());
    //    addSequential(new CoconutCloseCommand()); FIXME: need to add coconut (Arjav?)
    addSequential(new ElevatorPositionCommand(ElevatorSubsystem.Position.CARGO_MEDIUM));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.BiscuitPosition.DOWN));
    addSequential(new PressureSetCommand(VacuumSubsystem.VacuumPressure.CARGO));
    addSequential(new ElevatorPositionCommand(ElevatorSubsystem.Position.CARGO_PICKUP));
    addSequential(
        new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT)); // FIXME: activate or deactivate?
    addSequential(new ElevatorPositionCommand(ElevatorSubsystem.Position.CARGO_MEDIUM));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.BiscuitPosition.UP));
    addSequential(new ElevatorPositionCommand(ElevatorSubsystem.Position.STOW));
  }
}
