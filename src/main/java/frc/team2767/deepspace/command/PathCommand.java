package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.motion.PathController;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DriveSubsystem;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private PathController pathController;

  public PathCommand(String pathName) {

    logger.debug("PathCommand for {} init", pathName);
    pathController = new PathController(pathName);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    DRIVE.startPath(pathController);
  }

  @Override
  protected boolean isFinished() {
    return DRIVE.isPathFinished();
  }

  @Override
  protected void end() {

    DRIVE.endPath();
    logger.debug("PathCommand ending");
  }
}
