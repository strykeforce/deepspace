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
  private double cameraAngle;
  private double cameraRange;
  private double cameraX;
  private double cameraY;
  private double swerveRotation;

  private double headingAdjustment;

  public TwistCalculator(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double swerveRotation) {

    computeNew(cameraAngle, cameraRange, cameraX, cameraY, swerveRotation);
  }

  private void computeNew(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double swerveRotation) {

    deltaX = 0.0;
    deltaY = 0.0;
    headingAdjustment = 0.0;

    this.cameraAngle = cameraAngle;
    double transferSlope = 1.2449;
    double transferIntercept = -4.3949;
    this.cameraRange = transferSlope * cameraRange + transferIntercept;
    this.cameraX = cameraX;
    this.cameraY = cameraY;
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

    headingAdjustment = Math.atan2(deltaY, deltaX);
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
    return DRIVE.getGyro().getAngle() - headingAdjustment;
  }

  /** @return twist range */
  public double getRange() {
    return Math.hypot(deltaX, deltaY);
  }
}
