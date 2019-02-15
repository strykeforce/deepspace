package frc.team2767.deepspace.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwistCalculator {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double cameraX;
  private double cameraY;
  private double finalHeading;
  private double finalRange;
  private double cameraAngle;
  private double cameraRange;
  private double cameraPositionBearing;
  private double swerveRotation;
  private double targetYaw;

  public TwistCalculator(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double cameraPositionBearing,
      double swerveRotation,
      double targetYaw) {
    this.cameraAngle = cameraAngle;
    this.cameraRange = cameraRange;
    this.cameraX = cameraX;
    this.cameraY = cameraY;
    this.cameraPositionBearing = cameraPositionBearing;
    this.swerveRotation = swerveRotation;
    this.targetYaw = targetYaw;
    compute();
  }

  @SuppressWarnings("Duplicates")
  private void compute() {
    double deltaX;
    double deltaY;

    double transferSlope = 1.2449;
    double transferIntercept = -4.3949;
    double range = transferSlope * cameraRange + transferIntercept;

    double currentX =
        cameraX * Math.cos(Math.toRadians(swerveRotation))
            - cameraY * Math.sin(Math.toRadians(swerveRotation));

    double currentY =
        cameraY * Math.cos(Math.toRadians(swerveRotation))
            + cameraX * Math.sin(Math.toRadians(swerveRotation));

    double finalX =
        cameraX * Math.cos(Math.toRadians(targetYaw))
            - cameraY * Math.sin(Math.toRadians(targetYaw));

    double finalY =
        cameraY * Math.cos(Math.toRadians(targetYaw))
            + cameraX * Math.sin(Math.toRadians(targetYaw));

    deltaX = finalX - currentX;
    deltaY = finalY - currentY;

    double initialHeading = cameraPositionBearing + swerveRotation + cameraAngle;

    double headingX = range * Math.cos(Math.toRadians(initialHeading));
    double headingY = range * Math.sin(Math.toRadians(initialHeading));

    double xCorrected = headingX - deltaX;
    double yCorrected = headingY - deltaY;

    finalHeading = Math.toDegrees(Math.atan2(yCorrected, xCorrected));
    finalRange = Math.hypot(xCorrected, yCorrected);

    logger.debug("Driving range of {} at {}", finalRange, finalHeading);
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
