package frc.team2767.deepspace.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class TwistCalculatorTest {

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
        .isCloseTo(expectedRange, Percentage.withPercentage(0.1));
  }

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

    assertThat(twistCalculator.getRange()).isCloseTo(expectedRange, Percentage.withPercentage(0.1));
  }
}
