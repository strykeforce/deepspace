package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.coconut.CoconutCloseCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerStopCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
import frc.team2767.deepspace.command.vacuum.PressureAccumulateCommandGroup;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.subsystem.*;

public class CoconutPickupCommandGroup extends CommandGroup {

  public CoconutPickupCommandGroup() {
    addSequential(new PressureAccumulateCommandGroup());
    addSequential(new RollerStopCommand());
    addSequential(
        new CommandGroup() {
          {
            addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
            addParallel(new SetActionCommand(Action.PLACE));
            addParallel(new SetGamePieceCommand(GamePiece.CARGO));
            addParallel(new IntakePositionCommand(IntakeSubsystem.kMiddlePositionDeg));
            addParallel(new CoconutCloseCommand());
          }
        });

    addSequential(new LogCommand("exiting command group"));
    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoMediumPositionInches));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kDownRightPositionDeg));
    addParallel(new PressureSetCommand(VacuumSubsystem.kBallPressureInHg));

    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoPickupPositionInches));

    //    addSequential(new BiscuitWiggleCommand());
    // addSequential(new WaitForPressureCommand(VacuumSubsystem.kBallPressureInHg));
    // addSequential(new WaitCommand(0.5));
    addSequential(new LogCommand("opening valves"));
    addSequential(
        new ActivateValveCommand(
            new VacuumSubsystem.Valve[] {
              VacuumSubsystem.Valve.PUMP, VacuumSubsystem.Valve.TRIDENT
            }),
        5);
    addSequential(new LogCommand("opened valves"));
    addSequential(new WaitCommand(1.0));
    addSequential(new ElevatorSetPositionCommand(ElevatorSubsystem.kCargoMediumPositionInches));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
    addSequential(new SetActionCommand(Action.PLACE));
    //        addSequential(new
    // ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.kStowPositionDeg));
  }
}
