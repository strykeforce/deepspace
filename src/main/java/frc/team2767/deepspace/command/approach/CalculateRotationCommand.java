package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import frc.team2767.deepspace.util.RotationCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculateRotationCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public CalculateRotationCommand() {}

  @Override
  protected void initialize() {
    RotationCalculator rotationCalculator =
        new RotationCalculator(
            VISION.getRawBearing(),
            VISION.getRawRange(),
            VISION.getCameraPositionBearing(),
            Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360));

    VISION.setCorrectedHeading(rotationCalculator.getHeading());

    logger.debug("heading = {}", VISION.getCorrectedHeading());
  }
}
