package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.buttons.Trigger;

public class DirectionPadRight extends Trigger {

  private final GameControls controls;

  public DirectionPadRight(GameControls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return Math.abs(controls.getDPad() - 90) < 20;
  }
}
