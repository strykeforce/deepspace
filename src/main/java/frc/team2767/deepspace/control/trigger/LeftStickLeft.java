package frc.team2767.deepspace.control.trigger;

import edu.wpi.first.wpilibj.buttons.Trigger;
import frc.team2767.deepspace.control.GameControls;

public class LeftStickLeft extends Trigger {

  private final GameControls controls;

  public LeftStickLeft(GameControls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return controls.getLX() < -0.1;
  }
}
