package frc.team2767.deepspace.control.trigger;

import edu.wpi.first.wpilibj.buttons.Trigger;
import frc.team2767.deepspace.control.GameControls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectionPadAny extends Trigger {

  private final GameControls controls;

  public DirectionPadAny(GameControls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return controls.getDPad() != -1;
  }
}
