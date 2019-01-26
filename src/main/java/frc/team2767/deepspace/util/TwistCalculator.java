package frc.team2767.deepspace.util;

public class TwistCalculator {

  private double deltaX;
  private double deltaY;
  private double cameraAngle;
  private double cameraRange;
  private double cameraX;
  private double cameraY;
  private double swerveRotation;

  public TwistCalculator(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double swerveRotation) {

    setInputs(cameraAngle, cameraRange, cameraX, cameraY, swerveRotation);
  }

  private void setInputs(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double swerveRotation) {

    deltaX = 0.0;
    deltaY = 0.0;

    this.cameraAngle = cameraAngle;
    this.cameraRange = cameraRange;
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
    return Math.atan2(deltaY, deltaX);
  }

  /** @return twist range */
  public double getRange() {
    return Math.hypot(deltaX, deltaY);
  }

  /** @return twist yaw */
  public double getyaw() {
    return 0.0;
  }
}
