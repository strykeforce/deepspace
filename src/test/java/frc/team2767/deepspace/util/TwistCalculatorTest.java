package frc.team2767.deepspace.util;

import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.assertj.core.api.Assertions.assertThat;

class TwistCalculatorTest {

  private static final double ACCEPTABLE_RANGE = 1;
  private static final double ACCEPTABLE_HEADING = 1;

  @ParameterizedTest
  @CsvFileSource(resources = "/inputs.csv", numLinesToSkip = 1)
  void getHeading(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double cameraPositionBearing,
      double swerveRotation,
      double targetYaw,
      double expectedHeading,
      double expectedRange) {

    TwistCalculator twistCalculator =
        new TwistCalculator(
            cameraAngle,
            cameraRange,
            cameraX,
            cameraY,
            cameraPositionBearing,
            swerveRotation,
            targetYaw);

    assertThat(twistCalculator.getHeading())
        .isCloseTo(expectedHeading, Percentage.withPercentage(ACCEPTABLE_HEADING));
  }

  @Disabled
  @ParameterizedTest
  @CsvFileSource(resources = "/inputs.csv", numLinesToSkip = 1)
  void getRange(
      double cameraAngle,
      double cameraRange,
      double cameraX,
      double cameraY,
      double cameraPositionBearing,
      double swerveRotation,
      double targetYaw,
      double expectedHeading,
      double expectedRange) {

    TwistCalculator twistCalculator =
        new TwistCalculator(
            cameraAngle,
            cameraRange,
            cameraX,
            cameraY,
            cameraPositionBearing,
            swerveRotation,
            targetYaw);

    assertThat(twistCalculator.getRange()).isCloseTo(expectedRange, Percentage.withPercentage(ACCEPTABLE_RANGE));
  }
}
