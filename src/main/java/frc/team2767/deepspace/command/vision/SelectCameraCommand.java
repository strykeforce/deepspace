package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectCameraCommand extends Command {

  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  VisionSubsystem.Camera camera;

  public SelectCameraCommand(VisionSubsystem.Camera camera) {
    this.camera = camera;
  }

  @Override
  protected void initialize() {
    logger.debug("initialize");
  }

  @Override
  protected void execute() {
    super.execute();
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
