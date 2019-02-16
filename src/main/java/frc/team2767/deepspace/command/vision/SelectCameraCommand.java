package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectCameraCommand extends InstantCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  VisionSubsystem.Camera camera;

  public SelectCameraCommand() {}

  @Override
  protected void initialize() {
    if (-180 < DRIVE.getGyro().getAngle() && DRIVE.getGyro().getAngle() < 0) {
      camera = VisionSubsystem.Camera.LEFT;
    } else {
      camera = VisionSubsystem.Camera.RIGHT;
    }

    logger.debug("initialize");
  }
}
