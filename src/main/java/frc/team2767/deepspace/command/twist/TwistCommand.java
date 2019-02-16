package frc.team2767.deepspace.command.twist;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwistCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public TwistCommand() {
    requires(DRIVE);
    setInterruptible(true);
  }

  @Override
  protected void initialize() {
    double heading = VISION.getCorrectedHeading();
    int distance = (int) (DriveSubsystem.TICKS_PER_INCH * VISION.getCorrectedRange());
    double targetYaw = VISION.getTargetYaw();

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
