package frc.team2767.deepspace.command.sequences.place;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitMoveSafeCommand;
import frc.team2767.deepspace.command.biscuit.ReleaseKrakenCommand;
import frc.team2767.deepspace.command.elevator.ElevatorMoveToSafePositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.sequences.pickup.BallToHatchConditionalCommand;
import frc.team2767.deepspace.command.sequences.pickup.Biscuit270WrapConditionalCommand;

public class PositionExecuteCommandGroup extends CommandGroup {

  public PositionExecuteCommandGroup() {
    addSequential(new LogCommand("BEGIN POSITION EXECUTE"));
    addSequential(new ElevatorMoveToSafePositionCommand());
    addSequential(new BiscuitMoveSafeCommand());
    addSequential(new Biscuit270WrapConditionalCommand());
    addSequential(
        new CommandGroup() {
          {
            addParallel(new BallToHatchConditionalCommand());
            addParallel(new ReleaseKrakenCommand(true));
          }
        });
    addSequential(new LogCommand("END POSITION EXECUTE"));
  }
}
