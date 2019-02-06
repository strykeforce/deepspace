package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.motion.PathController;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathCommand extends Command {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private static final DriveSubsystem DRIVE = Robot.DriveSubsystem;

  private PathController pathController;
  private String pathName;

  public PathCommand(String pathName) {

    logger.debug("PathCommand for {} init", pathName);
    this.pathName = pathName;
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    pathController = new PathController(pathName);
  }

  @Override
  protected void execute() {
    pathController.run();
  }

  @Override
  protected void end() {
    logger.debug("PathCommand ending");
  }

  @Override
  protected boolean isFinished() {
    return !pathController.isRunning();
  }
}
