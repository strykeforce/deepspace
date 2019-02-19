package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.coconut.CoconutCloseCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerStopCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
import frc.team2767.deepspace.command.vacuum.DeactivateValveCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.subsystem.*;

public class CoconutPickupCommandGroup extends CommandGroup {

  public CoconutPickupCommandGroup() {
    addSequential(
        new DeactivateValveCommand(
            new VacuumSubsystem.Valve[] {
              VacuumSubsystem.Valve.PUMP, VacuumSubsystem.Valve.TRIDENT
            }));
    addSequential(
        new CommandGroup() {
          {
            addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.BiscuitPosition.UP));
            addSequential(new SetActionCommand(Action.PLACE));
            addSequential(new SetGamePieceCommand(GamePiece.CARGO));
            addSequential(new IntakePositionCommand(IntakeSubsystem.ShoulderPosition.UP));
            addSequential(new RollerStopCommand());
            addSequential(new CoconutCloseCommand());
            addSequential(
                new ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.CARGO_MEDIUM));
            addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.BiscuitPosition.DOWN_R));
          }
        });
    addParallel(new PressureSetCommand(VacuumSubsystem.VacuumPressure.CARGO));

    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.CARGO_PICKUP));
    addSequential(
        new ActivateValveCommand(
            new VacuumSubsystem.Valve[] {
              VacuumSubsystem.Valve.PUMP, VacuumSubsystem.Valve.TRIDENT
            }));
    addSequential(new WaitCommand(5.0));
    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.CARGO_MEDIUM));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.BiscuitPosition.UP));
    //        addSequential(new
    // ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.STOW));
  }
}
