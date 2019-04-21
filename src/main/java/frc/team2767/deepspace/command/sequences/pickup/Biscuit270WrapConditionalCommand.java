package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class Biscuit270WrapConditionalCommand extends ConditionalCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final VisionSubsystem VISION = Robot.VISION;

  private static double currentBiscuitPos;
  private static final double LEFT_270 = 260;
  private static final double RIGHT_270 = -260;

  public Biscuit270WrapConditionalCommand() {
    super(
        new CommandGroup() {
          {
            addSequential(
                new CommandGroup() {
                  {
                    addParallel(new ElevatorSetPositionCommand(22.0));
                    addParallel(new BiscuitExecutePlanCommand());
                  }
                });

            addSequential(new ElevatorExecutePlanCommand());
          }
        });
  }

  // If Biscuit is wrapped 270 and needs to switch sides, move elevator up, spin biscuit, and move
  // back down
  @Override
  protected boolean condition() {
    currentBiscuitPos = BISCUIT.getPosition();
    return (currentBiscuitPos > LEFT_270 && VISION.direction == FieldDirection.RIGHT)
        || (currentBiscuitPos < RIGHT_270 && VISION.direction == FieldDirection.LEFT);
  }
}
