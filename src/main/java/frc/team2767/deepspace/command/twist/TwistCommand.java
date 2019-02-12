package frc.team2767.deepspace.command.twist;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwistCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final double heading;
  private final int distance;
  private final double targetYaw;

  public TwistCommand(double heading, int distance, double targetYaw) {
    logger.debug("twist command constructor");
    this.heading = heading;
    this.distance = distance;
    this.targetYaw = targetYaw;
    requires(DRIVE);
    setInterruptible(true);
    logger.debug("finish constructor");
  }

  @Override
  protected void initialize() {
    logger.debug("heading={} distance={} targetYaw={}", heading, distance, targetYaw);
    DRIVE.startTwist(heading, distance, targetYaw);
  }

  @Override
  protected boolean isFinished() {
    return DRIVE.isTwistFinished();
  }

  @Override
  protected void interrupted() {
    logger.debug("twist command interrupted");
    DRIVE.interruptTwist();
  }
}
