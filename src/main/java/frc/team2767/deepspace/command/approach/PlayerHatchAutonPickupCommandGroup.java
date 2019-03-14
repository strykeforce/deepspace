package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.drive.OpenLoopDriveUntilSuctionCommand;
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

public class PlayerHatchAutonPickupCommandGroup extends CommandGroup {

  public PlayerHatchAutonPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN AUTON HATCH PICKUP"));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetActionCommand(Action.PICKUP));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
          }
        });
    addSequential(new LightsOnCommand());
    addSequential(new FieldSquarePickupAlignmentCommand());
    addSequential(new QueryPyeyeCommand());
    addSequential(new PositionExecuteCommandGroup());
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE));
    addSequential(new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg));
    addSequential(new CalculateTwistCommand());
    addSequential(new VisionTwistCommand());
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.GAME_PIECE_PICKUP));
    //    addSequential(new LogCommand("DriveUntilSuction here"));
    addSequential(
        new OpenLoopDriveUntilSuctionCommand(VacuumSubsystem.kHatchPressureInHg, -0.1, 0.0));
    addParallel(new BlinkLightsCommand(VisionSubsystem.LightPattern.GOT_HATCH));
    addSequential(new DriveTwistCommand(0, 50));

    addSequential(new LogCommand("END AUTON HATCH PICKUP"));
  }
}
