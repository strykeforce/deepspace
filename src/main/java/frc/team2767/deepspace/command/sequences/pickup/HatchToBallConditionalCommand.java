package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HatchToBallConditionalCommand extends ConditionalCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private static final double ELEVATOR_HEIGHT = 22.75;

  public HatchToBallConditionalCommand() {
    super(
        new CommandGroup() {
          {
            addParallel(new ElevatorSetPositionCommand(ELEVATOR_HEIGHT));
            addParallel(new BiscuitSetPositionCommand(BiscuitSubsystem.kDownPosition));
          }
        },
        new CommandGroup() {
          {
            addSequential(new ElevatorSetPositionCommand(ELEVATOR_HEIGHT));
            addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kDownPosition));
          }
        });
  }

  @Override
  protected boolean condition() {
    if (Math.abs(BISCUIT.getPosition()) > 120 && Math.abs(BISCUIT.getPosition()) < 240) {
      logger.debug("safe, position = {}", BISCUIT.getPosition());
      return true;
    }

    logger.debug("not safe", BISCUIT.getPosition());
    return false;
  }
}
