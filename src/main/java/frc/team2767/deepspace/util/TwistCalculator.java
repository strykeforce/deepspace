package frc.team2767.deepspace.util;

import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwistCalculator {

  private final DriveSubsystem DRIVE = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double cameraX;
  private double cameraY;
  private double finalHeading;
  private double finalRange;

  public TwistCalculator(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double cameraPositionBearing,
      double swerveRotation,
      double targetYaw) {

    double deltaX;
    double deltaY;

    double transferSlope = 1.2449;
    double transferIntercept = -4.3949;
    double range = transferSlope * cameraRange + transferIntercept;
    //    double range = cameraRange;
    this.cameraX = cameraX;
    this.cameraY = cameraY;

    logger.debug(
        "9 * Math.sin(Math.toRadians(targetYaw)) = -9.0 = {}",
        9.0 * Math.sin(Math.toRadians(-90.0)));

    double currentX =
        cameraX * Math.cos(Math.toRadians(swerveRotation))
            - cameraY * Math.sin(Math.toRadians(swerveRotation));

    logger.debug(
        "cX = {} = {} - {}",
        currentX,
        cameraX * Math.cos(Math.toRadians(swerveRotation)),
        cameraY * Math.sin(Math.toRadians(swerveRotation)));

    double currentY =
        cameraY * Math.cos(Math.toRadians(swerveRotation))
            + cameraX * Math.sin(Math.toRadians(swerveRotation));

    logger.debug(
        "cY = {} = {} + {}",
        currentY,
        cameraY * Math.cos(Math.toRadians(swerveRotation)),
        cameraX * Math.sin(Math.toRadians(swerveRotation)));

    double finalX =
        cameraX * Math.cos(Math.toRadians(targetYaw))
            - cameraY * Math.sin(Math.toRadians(targetYaw));

    logger.debug(
        "finalX = {} = {} - {}",
        finalX,
        cameraX * Math.cos(Math.toRadians(targetYaw)),
        cameraY * Math.sin(Math.toRadians(targetYaw)));

    double finalY =
        cameraY * Math.cos(Math.toRadians(targetYaw))
            + cameraX * Math.sin(Math.toRadians(targetYaw));

    logger.debug(
        "finalY = {} = {} + {}",
        finalY,
        cameraY * Math.cos(Math.toRadians(targetYaw)),
        cameraX * Math.sin(Math.toRadians(targetYaw)));

    //    logger.debug("cX={} cY={}", currentX, currentY);
    //    logger.debug("fX={} fY={}", finalX, finalY);

    deltaX = finalX - currentX;
    deltaY = finalY - currentY;

    logger.debug("dX={} dY={}", deltaX, deltaY);

    double initialHeading = cameraPositionBearing + DRIVE.getGyro().getAngle() + cameraAngle;

    logger.debug("original heading={}", initialHeading);

    double headingX = range * Math.cos(Math.toRadians(initialHeading));
    double headingY = range * Math.sin(Math.toRadians(initialHeading));

    logger.debug("headingX={} headingY={}", headingX, headingY);

    double xCorrected = headingX - deltaX;
    double yCorrected = headingY - deltaY;

    logger.debug("xCorrected={} yCorrected={}", xCorrected, yCorrected);

    finalHeading = Math.toDegrees(Math.atan2(yCorrected, xCorrected));

    finalRange = Math.hypot(xCorrected, yCorrected);

    logger.debug("finalHeading = {} finalRange = {}", finalHeading, finalRange);
  }

  /** @return twist heading */
  public double getHeading() {
    return finalHeading;
  }

  /** @return twist range */
  public double getRange() {
    return finalRange;
  }
}
