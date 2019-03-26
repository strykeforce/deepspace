package frc.team2767.deepspace.command.approach.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.CalculateRotationCommand;
import frc.team2767.deepspace.command.approach.OpenLoopDriveUntilSuctionCommand;
import frc.team2767.deepspace.command.approach.TalonConfigCommand;
import frc.team2767.deepspace.command.approach.YawToTargetCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.command.vision.BlinkLightsCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.command.vision.QueryPyeyeCommand;
import frc.team2767.deepspace.subsystem.*;

public class HatchPickupCommandGroup extends CommandGroup {

  public HatchPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN AUTO SNAP DRIVE HATCH PICKUP"));
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

    addSequential(new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg));
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.GAME_PIECE_PICKUP));
    addSequential(new TalonConfigCommand(DriveSubsystem.DriveTalonConfig.YAW_CONFIG));
    addSequential(new QueryPyeyeCommand());
    addSequential(new CalculateRotationCommand());
    addSequential(
        new CommandGroup() {
          {
            addParallel(new YawToTargetCommand());
            addParallel(new ElevatorExecutePlanCommand());
            addParallel(new BiscuitExecutePlanCommand());
          }
        });
    addSequential(new TalonConfigCommand(DriveSubsystem.DriveTalonConfig.DRIVE_CONFIG));
    addSequential(new OpenLoopDriveUntilSuctionCommand(), 10);
    addParallel(new BlinkLightsCommand(VisionSubsystem.LightPattern.GOT_HATCH), 0.5);
    addParallel(new SetActionCommand(Action.PLACE));
    addSequential(new LogCommand("END AUTO SNAP DRIVE HATCH PICKUP"));
  }
}
