package frc.team2767.deepspace.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class VectorRateLimitTest {

  private static double[] fwdOutput;
  private static double[] strOutput;
  private static final double TOLERANCE = 1;

  @ParameterizedTest
  @CsvFileSource(resources = "/sticks.csv", numLinesToSkip = 1)
  void getFwd(
      double rateLimit, double forward, double strafe, double expectedFwd, double expectedStr) {
    VectorRateLimit vectorLimit = new VectorRateLimit(rateLimit);

    fwdOutput = vectorLimit.apply(forward, strafe);

    assertThat(fwdOutput[0]).isCloseTo(expectedFwd, Percentage.withPercentage(TOLERANCE));
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/sticks.csv", numLinesToSkip = 1)
  void getStr(
      double rateLimit, double forward, double strafe, double expectedFwd, double expectedStr) {
    VectorRateLimit vectorRateLimit = new VectorRateLimit(rateLimit);

    strOutput = vectorRateLimit.apply(forward, strafe);

    assertThat(strOutput[1]).isCloseTo(expectedStr, Percentage.withPercentage(TOLERANCE));
  }
}
