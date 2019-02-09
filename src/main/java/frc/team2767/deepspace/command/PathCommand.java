package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DriveSubsystem;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private double targetYaw;

  public PathCommand(String pathName, double targetYaw) {
    logger.debug("PathCommand for {} constructor", pathName);
    requires(DRIVE);
    this.targetYaw = targetYaw;
    setInterruptible(true);
  }

  @Override
  protected void initialize() {
    DRIVE.startPath("loading_to_cargo", targetYaw);
  }

  @Override
  protected boolean isFinished() {
    return DRIVE.isPathFinished();
  }

  @Override
  protected void interrupted() {
    logger.debug("PathCommand interrupted");
    DRIVE.interruptPath();
  }
}
