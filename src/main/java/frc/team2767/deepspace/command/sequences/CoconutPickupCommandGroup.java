package frc.team2767.deepspace.command.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorOpenLoopDownCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.command.intake.RollerOutCommand;
import frc.team2767.deepspace.command.intake.RollerStopCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
import frc.team2767.deepspace.command.vacuum.PressureAccumulateCommandGroup;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.WaitForPressureCommand;
import frc.team2767.deepspace.subsystem.*;

public class CoconutPickupCommandGroup extends CommandGroup {

  public CoconutPickupCommandGroup() {
    addSequential(new RollerOutCommand(0.2));
    addParallel(new PressureAccumulateCommandGroup());
    addParallel(new PressureSetCommand(VacuumSubsystem.kBallPressureInHg));

    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetActionCommand(Action.PLACE));
            addParallel(new SetGamePieceCommand(GamePiece.CARGO));
            addParallel(new IntakePositionCommand(IntakeSubsystem.kMiddlePositionDeg));
          }
        });

    addSequential(new ElevatorSetPositionCommand(22.0));
    addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kDownRightPositionDeg));

    addSequential(new ElevatorSetPositionCommand(18.0));
    addParallel(new IntakePositionCommand(105)); // 105
    addSequential(new LogCommand("opening valves"));
    addSequential(
        new ActivateValveCommand(
            new VacuumSubsystem.Valve[] {
              VacuumSubsystem.Valve.PUMP, VacuumSubsystem.Valve.TRIDENT
            }),
        5);
    addSequential(new LogCommand("opened valves"));
    addSequential(new ElevatorOpenLoopDownCommand());
    addSequential(new WaitForPressureCommand(VacuumSubsystem.kBallPressureInHg));
    addSequential(new ElevatorSetPositionCommand(25.0));
    addSequential(new SetActionCommand(Action.PLACE));
    addSequential(new RollerStopCommand());
    //        addSequential(new
    // ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition.kStowPositionDeg));
  }
}
