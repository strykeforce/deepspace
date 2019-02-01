package frc.team2767.deepspace.util;

import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwistCalculator {

  private final double distanceSafetyAdjustment = 10.0;
  private final DriveSubsystem DRIVE = Robot.DriveSubsystem;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double deltaX;
  private double deltaY;
  private double cameraAngle;
  private double cameraRange;
  private double cameraX;
  private double cameraY;
  private double cameraPositionBearing;
  private double swerveRotation;

  public TwistCalculator(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double cameraPositionBearing,
      double swerveRotation) {

    computeNew(cameraAngle, cameraRange, cameraX, cameraY, cameraPositionBearing, swerveRotation);
  }

  private void computeNew(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double cameraPositionBearing,
      double swerveRotation) {

    deltaX = 0.0;
    deltaY = 0.0;

    this.cameraAngle = cameraAngle;
    double transferSlope = 1.2449;
    double transferIntercept = -4.3949;
    this.cameraRange = transferSlope * cameraRange + transferIntercept;
    this.cameraX = cameraX;
    this.cameraY = cameraY;
    this.cameraPositionBearing = cameraPositionBearing;
    this.swerveRotation = swerveRotation;

    targetToCamera();
    cameraToRobot();
    robotToSwerve();
  }

  private void targetToCamera() {
    deltaX = (cameraRange * Math.cos(Math.toRadians(cameraAngle)));
    deltaY = (cameraRange * Math.sin(Math.toRadians(cameraAngle)));
  }

  private void cameraToRobot() {
    deltaX += cameraX;
    deltaY += cameraY;
  }

  private void robotToSwerve() {
    double dXtemp = deltaX;
    deltaX =
        deltaX * Math.cos(Math.toRadians(swerveRotation))
            - deltaY * Math.sin(Math.toRadians(swerveRotation));
    deltaY =
        deltaY * Math.cos(Math.toRadians(swerveRotation))
            + dXtemp * Math.sin(Math.toRadians(swerveRotation));
  }

  /** @return twist heading */
  public double getHeading() {
    return (cameraPositionBearing + DRIVE.getGyro().getAngle() + cameraAngle);
  }

  /** @return twist range */
  public double getRange() {
    return Math.hypot(deltaX, deltaY) - distanceSafetyAdjustment;
  }
}
