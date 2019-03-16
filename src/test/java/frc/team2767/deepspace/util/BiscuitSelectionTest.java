package frc.team2767.deepspace.util;

import static frc.team2767.deepspace.subsystem.FieldDirection.*;
import static org.assertj.core.api.Assertions.assertThat;

import frc.team2767.deepspace.subsystem.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class BiscuitSelectionTest {

  double selectedAngle = -2767;
  ElevatorLevel targetLevel;
  GamePiece currentGamePiece;
  Action currentAction;
  FieldDirection targetDirection;

  @ParameterizedTest
  @CsvFileSource(resources = "/biscuit.csv", numLinesToSkip = 1)
  void selectAngle(
      ElevatorLevel targetLevel,
      GamePiece currentGamePiece,
      Action currentAction,
      FieldDirection targetDirection,
      double expectedAngle) {

    this.targetLevel = targetLevel;
    this.currentGamePiece = currentGamePiece;
    this.currentAction = currentAction;
    this.targetDirection = targetDirection;

    selectedAngle = doSelection();

    assertThat(selectedAngle).isEqualTo(expectedAngle);
  }

  public double doSelection() {
    if (currentAction == Action.PLACE
        && currentGamePiece == GamePiece.CARGO
        && targetLevel == ElevatorLevel.THREE) {
      if (targetDirection == LEFT) {
        return -75;
      }
      if (targetDirection == RIGHT) {
        return 75;
      } else {
        System.out.println("Direction not set");
      }
    }

    if (currentAction == Action.PICKUP && currentGamePiece == GamePiece.CARGO) {
      double bearing = Math.IEEEremainder(-20, 360); // hardcoding not ideal
      if (bearing <= 0) {
        return 135;
      } else {
        return -135;
      }
    }

    if (targetDirection == LEFT) {
      return -90;
    }
    if (targetDirection == RIGHT) {
      return 90;
    }
    System.out.println("Target direction = " + targetDirection);
    return 2767;
  }
}
