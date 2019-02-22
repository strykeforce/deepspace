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
  private static final VisionSubsystem VISION = Robot.VISION;

  public SelectCameraCommand() {
    requires(DRIVE);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    VisionSubsystem.Camera camera;

    if (Math.abs(DRIVE.getGyro().getYaw()) < 90) {
      camera = VisionSubsystem.Camera.LEFT;
    } else {
      camera = VisionSubsystem.Camera.RIGHT;
    }

    VISION.setCamera(camera);
  }
}
