package frc.team2767.deepspace.command.twist;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrthogonalMovementCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public OrthogonalMovementCommand() {
    setInterruptible(true);
    requires(DRIVE);
  }

  @SuppressWarnings("Duplicates")
  @Override
  protected void initialize() {
    VISION.queryPyeye();
    VISION.setCamera(VisionSubsystem.Camera.LEFT);
    double heading = VISION.getRawBearing();
    int distance = (int) (DriveSubsystem.TICKS_PER_INCH * VISION.getRawRange());

    distance = (int) (distance * Math.cos(Math.toRadians(90.0 - heading)));

    logger.debug("dist = {}", distance / DriveSubsystem.TICKS_PER_INCH);

    double direction = DRIVE.getGyro().getAngle();

    if (distance < 0) {
      distance *= -1;
      direction += 180;
    }

    DRIVE.startTwist(direction, distance, DRIVE.getGyro().getAngle());
  }

  @Override
  protected boolean isFinished() {
    return DRIVE.isTwistFinished();
  }

  @Override
  protected void interrupted() {
    DRIVE.interruptTwist();
  }
}
