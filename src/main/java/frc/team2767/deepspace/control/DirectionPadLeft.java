package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.buttons.Trigger;

public class DirectionPadLeft extends Trigger {

  private final GameControls controls;

  public DirectionPadLeft(GameControls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return controls.getDPad() == 270;
  }
}
