package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.biscuit.BiscuitPositionAboveCameraCommand;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BallToHatchAutoSafetyCommand extends ConditionalCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  public BallToHatchAutoSafetyCommand() {
    super(
        new CommandGroup() {
          {
            addParallel(new BiscuitPositionAboveCameraCommand());
            addParallel(new ElevatorExecutePlanCommand());
          }
        },
        new CommandGroup() {
          {
            addSequential(new ElevatorSetPositionCommand(25.0));
            addSequential(new BiscuitPositionAboveCameraCommand());
            addSequential(new ElevatorExecutePlanCommand());
          }
        });
  }

  @Override
  protected boolean condition() {
    return Math.abs(BISCUIT.getPosition()) < 120; // is safe
  }
}
