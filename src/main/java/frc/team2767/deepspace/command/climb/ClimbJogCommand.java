package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClimbJogCommand extends Command {

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
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

  @Override
  protected boolean isFinished() {
    return CLIMB.getStringPotPosition() < (ClimbSubsystem.kHighRelease - 10);
  }

  @Override
  protected void end() {
    CLIMB.stop();
  }
}
