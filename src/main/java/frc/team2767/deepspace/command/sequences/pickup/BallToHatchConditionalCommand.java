package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.biscuit.BiscuitExecutePlanCommand;
import frc.team2767.deepspace.command.elevator.ElevatorExecutePlanCommand;
import frc.team2767.deepspace.command.intake.IntakePositionCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BallToHatchConditionalCommand extends ConditionalCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public BallToHatchConditionalCommand() {
    super(
        new CommandGroup() {
          {
            addParallel(new ElevatorExecutePlanCommand());
            addParallel(new BiscuitExecutePlanCommand());
          }
        },
        new CommandGroup() {
          {
            addSequential(
                new CommandGroup() {
                  {
                    addParallel(new BiscuitExecutePlanCommand());
                    addParallel(new IntakePositionCommand(IntakeSubsystem.kStowPositionDeg));
                  }
                });
            addSequential(new ElevatorExecutePlanCommand());
          }
        });
  }

  @Override
  protected boolean condition() {
    if (Math.abs(BISCUIT.getPosition()) < 120) {
      logger.debug("safe, position = {}", BISCUIT.getPosition());
      return true;
    }

    logger.debug("not safe", BISCUIT.getPosition());
    return false;
  }
}
