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
  private double cameraPositionBearing;

  public TwistCalculator(double cameraAngle, double cameraRange, double cameraPositionBearing) {

    computeNew(cameraAngle, cameraRange, cameraPositionBearing);
  }

  private void computeNew(double cameraAngle, double cameraRange, double cameraPositionBearing) {

    deltaX = 0.0;
    deltaY = 0.0;

    this.cameraAngle = cameraAngle;
    double transferSlope = 1.2449;
    double transferIntercept = -4.3949;
    this.cameraRange = transferSlope * cameraRange + transferIntercept;
    this.cameraPositionBearing = cameraPositionBearing;

    targetToCamera();
  }

  private void targetToCamera() {
    deltaX = (cameraRange * Math.cos(Math.toRadians(cameraAngle)));
    deltaY = (cameraRange * Math.sin(Math.toRadians(cameraAngle)));
  }

  /** @return twist heading */
  public double getHeading() {
    return (cameraPositionBearing + DRIVE.getGyro().getAngle() + cameraAngle);
  }

  /** @return twist range */
  public double getRange() {
    return Math.hypot(deltaX, deltaY);
  }
}
