package frc.team2767.deepspace.command.approach.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.approach.ApproachDirectionCommand;
import frc.team2767.deepspace.command.approach.HoldHeadingUntilSuctionCommand;
import frc.team2767.deepspace.command.approach.VisionAutoAlignPickupCommand;
import frc.team2767.deepspace.command.biscuit.*;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.states.SetActionCommand;
import frc.team2767.deepspace.command.states.SetGamePieceCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import frc.team2767.deepspace.subsystem.*;

public class AutoHatchPickupCommandGroup extends CommandGroup {

  public AutoHatchPickupCommandGroup() {
    addSequential(new LogCommand("BEGIN AUTO HATCH PICKUP"));
    addSequential(new ApproachDirectionCommand());
    addSequential(new LightsOnCommand());
    addSequential(
        new CommandGroup() {
          {
            addParallel(new SetActionCommand(Action.PICKUP));
            addParallel(new SetGamePieceCommand(GamePiece.HATCH));
            addParallel(new SetLevelCommand(ElevatorLevel.ONE));
          }
        });

    addSequential(new PressureSetCommand(VacuumSubsystem.kHatchPressureInHg), 0.02);
    addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.GAME_PIECE_PICKUP));
    addParallel(new ReleaseKrakenCommand(false)); //Don't want kraken in teleop
    addParallel(new BiscuitPositionAboveCameraCommand());
    addParallel(new ElevatorExecutePlanCommand());
    addSequential(new VisionAutoAlignPickupCommand());
    addSequential(new BiscuitFastExecuteCommand());
    addSequential(new HoldHeadingUntilSuctionCommand());
    addParallel(new BiscuitConfigMotionAccelCommand(BiscuitSubsystem.kSlowAccel));
    addParallel(new SetActionCommand(Action.PLACE));
    addSequential(new LogCommand("END AUTO HATCH PICKUP"));
  }
}
