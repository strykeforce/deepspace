package frc.team2767.deepspace.util;

import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwistCalculator {

  private final DriveSubsystem DRIVE = Robot.DriveSubsystem;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double deltaX;
  private double deltaY;
  private double cameraX;
  private double targetYaw;
  private double cameraY;
  private double swerveRotation;
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

    deltaX = 0.0;
    deltaY = 0.0;

    double transferSlope = 1.2449;
    double transferIntercept = -4.3949;
    this.targetYaw = targetYaw;
    double range = transferSlope * cameraRange + transferIntercept;
    this.cameraX = cameraX;
    this.cameraY = cameraY;
    this.swerveRotation = swerveRotation;

    cameraYawCorrection();

    double initialHeading = cameraPositionBearing + DRIVE.getGyro().getAngle() + cameraAngle;
    double headingX = range * Math.cos(Math.toRadians(initialHeading));
    double headingY = range * Math.sin(Math.toRadians(initialHeading));

    double xCorrected = headingX - deltaX;
    double yCorrected = headingY - deltaY;

    finalHeading = Math.toDegrees(Math.atan2(yCorrected, xCorrected));
    finalRange = Math.hypot(xCorrected, yCorrected);
  }

  private void cameraYawCorrection() {
    double currentX = rotateX(swerveRotation);
    double currentY = rotateY(swerveRotation);

    double finalX = rotateX(targetYaw);
    double finalY = rotateY(targetYaw);

    deltaX = finalX - currentX;
    deltaY = finalY - currentY;
  }

  private double rotateX(double angle) {
    return cameraX * Math.cos(Math.toRadians(angle)) - cameraY * Math.sin(Math.toRadians(angle));
  }

  private double rotateY(double angle) {
    return cameraY * Math.cos(Math.toRadians(angle)) + cameraX * Math.sin(Math.toRadians(angle));
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
