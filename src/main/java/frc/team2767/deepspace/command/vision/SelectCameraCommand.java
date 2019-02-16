package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectCameraCommand extends InstantCommand {

  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private static final VisionSubsystem VISION = Robot.VISION;
  VisionSubsystem.Camera camera;

  public SelectCameraCommand() {}

  @Override
  protected void initialize() {

    VISION.setCamera(camera);
  }
}
