package frc.team2767.deepspace.util;

import static frc.team2767.deepspace.subsystem.FieldDirection.LEFT;

import frc.team2767.deepspace.subsystem.FieldDirection;

public class AutoPlaceSideChooser {

  public AutoPlaceSideChooser() {}

  public double determineGyroOffset(FieldDirection direction, double gyroAngle) {
    double offset = 0.5;

    if (direction == LEFT) {
      if (gyroAngle >= 45.0 && gyroAngle < 135.0) {
        offset = 0.0; // cargo ship front
      } else if (gyroAngle >= 135.0 || gyroAngle <= -90.0) {
        offset = -90.0; // cargo ship left
      } else if (gyroAngle > -90.0 && gyroAngle < 45.0) {
        offset = 90.0; // cargo ship right
      }
    } else {
      if (gyroAngle > -135.0 && gyroAngle <= -45.0) {
        offset = 0.0; // cargo ship front
      } else if (gyroAngle > -45.0 && gyroAngle < 90.0) {
        offset = -90.0; // cargo ship left
      } else if (gyroAngle <= -135.0 || gyroAngle >= 90.0) {
        offset = 90.0; // cargo ship right
      }
    }

    return offset;
  }

  public double determineTargetYaw(FieldDirection direction) {
    double target;

    if (direction == LEFT) {
      target = 90.0;
    } else {
      target = -90.0;
    }

    return target;
  }
}
