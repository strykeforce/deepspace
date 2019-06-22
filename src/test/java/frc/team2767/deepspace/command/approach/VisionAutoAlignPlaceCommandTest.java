package frc.team2767.deepspace.command.approach;

import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.util.AutoPlaceSideChooser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.assertj.core.api.Assertions.assertThat;

class VisionAutoAlignPlaceCommandTest {

  AutoPlaceSideChooser autoPlaceSideChooser = new AutoPlaceSideChooser();

  @ParameterizedTest
  @CsvFileSource(resources = "/autoalign.csv", numLinesToSkip = 1)
  void determineGyroOffset(String direction, double gyroAngle, double gyroOffset) {

    double offset = 0.0;

    if (direction.equals("L")) {
      offset = autoPlaceSideChooser.determineGyroOffset(FieldDirection.LEFT, gyroAngle);
    } else if (direction.equals("R")) {
      offset = autoPlaceSideChooser.determineGyroOffset(FieldDirection.RIGHT, gyroAngle);
    }

    assertThat(offset).isEqualTo(gyroOffset);
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/autoalign.csv", numLinesToSkip = 1)
  void determineTargetYaw(String direction, double gyroAngle, double gyroOffset, double targetYaw) {
    double target = 0.0;

    if (direction.equals("L")) {
      target = autoPlaceSideChooser.determineTargetYaw(FieldDirection.LEFT);
    } else if (direction.equals("R")) {
      target = autoPlaceSideChooser.determineTargetYaw(FieldDirection.RIGHT);
    }

    assertThat(target).isEqualTo(targetYaw);
  }
}
