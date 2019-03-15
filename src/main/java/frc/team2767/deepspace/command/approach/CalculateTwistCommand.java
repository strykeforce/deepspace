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
  private static final double TRIDENT_DISTANCE_OFFSET = 20.0;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public CalculateTwistCommand() {
    requires(DRIVE);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    double targetYaw = 90.0;

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
            VISION.getCameraPositionBearing(),
            yaw,
            targetYaw);

    double finalHeading = twistCalculator.getHeading();
    double finalRange = twistCalculator.getRange();

    double angle = finalHeading - VISION.getCameraPositionBearing();
    double newX = finalRange * Math.cos(Math.toRadians(angle));
    double newY = finalRange * Math.sin(Math.toRadians(angle)) - TRIDENT_DISTANCE_OFFSET;

    logger.debug("x={} y={}", newX, newY);
    finalRange = Math.hypot(newX, newY);
    finalHeading = Math.atan2(newY, newX) + VISION.getCameraPositionBearing();

    VISION.setCorrectedHeading(finalHeading);
    VISION.setCorrectedRange(finalRange);

    logger.debug(
        "corrected heading = {} distance = {}",
        VISION.getCorrectedHeading(),
        VISION.getCorrectedRange());
  }
}
