package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriveTwistCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private double inches;
  private double targetYaw;
  private double heading;

  public DriveTwistCommand(double heading, double inches) {
    this(heading, inches, Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360));
  }

  public DriveTwistCommand(double heading, double inches, double targetYaw) {

    this.heading = heading;
    this.inches = inches;
    this.targetYaw = targetYaw;

    setInterruptible(true);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    int distance = (int) (DriveSubsystem.TICKS_PER_INCH * inches);
    logger.debug("init: heading={} distance={} targetYaw={}", heading, distance, targetYaw);
    DRIVE.startTwist(heading, distance, targetYaw);
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
