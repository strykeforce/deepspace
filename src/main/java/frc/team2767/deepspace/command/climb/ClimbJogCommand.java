package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClimbJogCommand extends InstantCommand {

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double percent;

  public ClimbJogCommand(double percent) {
    this.percent = percent;
  }

  @Override
  protected void initialize() {
    logger.info("open loop climb at {}", percent);
    CLIMB.openLoop(percent);
  }
}
