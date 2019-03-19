package frc.team2767.deepspace.util;

public class RotationCalculator {
  private double finalHeading;
  private double cameraAngle;
  private double cameraRange;
  private double cameraPositionBearing;
  private double swerveRotation;

  public RotationCalculator(
      double cameraAngle, double cameraRange, double cameraPositionBearing, double swerveRotation) {
    this.cameraAngle = cameraAngle;
    this.cameraRange = cameraRange;
    this.cameraPositionBearing = cameraPositionBearing;
    this.swerveRotation = swerveRotation;
    compute();
  }

  @SuppressWarnings("Duplicates")
  private void compute() {
    double transferSlope = 1.2449;
    double transferIntercept = -4.3949;
    double range = transferSlope * cameraRange + transferIntercept;

    double initialHeading = cameraPositionBearing + swerveRotation + cameraAngle;

    double rangeX = range * Math.cos(Math.toRadians(initialHeading));
    double rangeY = range * Math.sin(Math.toRadians(initialHeading));

    finalHeading = Math.toDegrees(Math.atan2(rangeY, rangeX));
  }

  /** @return twist heading */
  public double getHeading() {
    return finalHeading;
  }
}
