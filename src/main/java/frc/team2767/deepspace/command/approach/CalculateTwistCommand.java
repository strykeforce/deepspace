package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import frc.team2767.deepspace.util.TwistCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculateTwistCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double offset;

  public CalculateTwistCommand(double offset) {
    this.offset = offset;
  }

  @Override
  protected void initialize() {
    double targetYaw = -90.0;
    double cameraPositionBearing = VISION.getCameraPositionBearing();

    double yaw = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360);

    logger.info(
        "pyeye bearing={} range={} current gyro = {}",
        VISION.getRawBearing(),
        VISION.getRawRange(),
        yaw);

    TwistCalculator twistCalculator =
        new TwistCalculator(
            VISION.getRawBearing(),
            VISION.getRawRange(),
            VISION.getCameraX(),
            VISION.getCameraY(),
            cameraPositionBearing,
            yaw,
            targetYaw);

    double offsetX = offset * Math.cos(Math.toRadians(targetYaw + cameraPositionBearing));
    double offsetY = offset * Math.sin(Math.toRadians(targetYaw + cameraPositionBearing));

    double[] corrected = twistCalculator.getXYCorrected();

    double newX = corrected[0] - offsetX;
    double newY = corrected[1] - offsetY;

    logger.debug("x={} y={}", newX, newY);

    double finalRange = Math.hypot(newX, newY);
    double finalHeading = Math.toDegrees(Math.atan2(newY, newX));

    VISION.setCorrectedHeading(finalHeading);
    VISION.setCorrectedRange(finalRange);

    logger.debug(
        "corrected heading = {} distance = {}",
        VISION.getCorrectedHeading(),
        VISION.getCorrectedRange());
  }
}
