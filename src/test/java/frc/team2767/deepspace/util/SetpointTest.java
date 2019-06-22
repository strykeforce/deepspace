package frc.team2767.deepspace.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class SetpointTest {

  @ParameterizedTest
  @CsvFileSource(resources = "/setpoints.csv", numLinesToSkip = 1)
  void setpointTest(
      double initial, double target, double progress, double targetProgress, double setpoint) {
    Setpoint s = new Setpoint(initial, target, targetProgress);

    assertThat(s.getSetpoint(progress)).isEqualTo(setpoint);
  }
}
