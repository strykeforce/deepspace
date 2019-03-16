package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.sequences.PositionExecuteCommandGroup;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.command.vision.BlinkLightsCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.subsystem.*;

public class HatchSnapPickupCommandGroup extends CommandGroup {

  public HatchSnapPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN ATUO SNAP HATCH PICKUP"));
    addSequential(new LightsOnCommand());
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetActionCommand(Action.PICKUP));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
          }
        });

    //    addSequential(new WaitCommand(0.2));
    addSequential(new QueryPyeyeCommand());

    addSequential(new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg));
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.GAME_PIECE_PICKUP));
    addSequential(new CalculateRotationCommand());
    addSequential(new YawToTargetCommand());
    addSequential(new PositionExecuteCommandGroup());
    addSequential(
        new OpenLoopDriveUntilSuctionCommand(VacuumSubsystem.kHatchPressureInHg, 0.20), 10);
    addParallel(new BlinkLightsCommand(VisionSubsystem.LightPattern.GOT_HATCH), 0.5);
    addSequential(new DriveTwistCommand(0, 40));

    addSequential(new LogCommand("END AUTO SNAP HATCH PICKUP"));
  }
}
//    addSequential(new FieldSquarePickupAlignmentCommand());
//    addSequential(new CalculateTwistCommand(30.0));
//    addSequential(new VisionTwistCommand());
