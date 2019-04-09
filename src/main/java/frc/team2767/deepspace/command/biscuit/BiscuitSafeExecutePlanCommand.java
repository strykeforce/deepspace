package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitSafeExecutePlanCommand extends ConditionalCommand {
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static double elevatorPosition;
  private static double biscuitPosition;
  private static boolean isWrapped;

  public BiscuitSafeExecutePlanCommand() {
    super(
        new CommandGroup() {
          {
            addSequential(new ElevatorSetPositionCommand(23.0));
            addSequential(new BiscuitExecutePlanCommand());
          }
        },
        new BiscuitExecutePlanCommand());
  }

  @Override
  protected boolean condition() {
    elevatorPosition = ELEVATOR.getPosition();
    biscuitPosition = BISCUIT.getPosition();
    isWrapped = elevatorPosition < 20.0 && Math.abs(biscuitPosition) > 185;
    if (isWrapped) logger.info("Moving Elevator to Execute Biscuit Plan");
    return isWrapped;
  }
}
