package frc.team2767.deepspace.command.deliver;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryPyeyeCommand extends Command {

  private static final VisionSubsystem VISION = Robot.VISION;
  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);

  @Override
  protected void initialize() {
    logger.debug("Looking for target");
  }

  @Override
  protected void execute() {
    VISION.queryPyeye();
  }

  @Override
  protected boolean isFinished() {
    return VISION.isTargetAcquired();
  }
}
