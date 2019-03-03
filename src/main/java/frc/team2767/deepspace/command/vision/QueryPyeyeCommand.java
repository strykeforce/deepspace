package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryPyeyeCommand extends Command {

  private static final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public QueryPyeyeCommand() {
    requires(VISION);
  }

  @Override
  protected void initialize() {
    logger.info("Looking for target");
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
