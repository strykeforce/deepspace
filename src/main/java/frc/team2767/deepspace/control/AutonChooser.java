package frc.team2767.deepspace.control;

import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static frc.team2767.deepspace.subsystem.StartSide.LEFT;
import static frc.team2767.deepspace.subsystem.StartSide.RIGHT;

public class AutonChooser {
  public static final VisionSubsystem VISION = Robot.VISION;
  private static final int AUTON_SWITCH_DEBOUNCED = 100;
  private static final Controls controls = Robot.CONTROLS;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private int newAutonSwitchPosition;
  private int autonSwitchStableCount = 0;
  private int autonSwitchPosition = -1;

  public AutonChooser() {}

  public void checkSwitch() {
    if (getStartPosition()) {
      logger.info(
          "auton switch initializing position {}, side = {}",
          String.format("%02X", autonSwitchPosition),
          VISION.startSide);
    }
  }

  private boolean getStartPosition() {
    boolean changed = false;
    int switchPosition = controls.getAutonSwitch().position();

    if (switchPosition != newAutonSwitchPosition) {
      autonSwitchStableCount = 0;
      newAutonSwitchPosition = switchPosition;
    } else {
      autonSwitchStableCount++;
    }

    if (autonSwitchStableCount > AUTON_SWITCH_DEBOUNCED && autonSwitchPosition != switchPosition) {
      changed = true;

      switch (autonSwitchPosition) {
        case 1:
          VISION.startSide = LEFT;
        case 2:
          VISION.startSide = RIGHT;
      }
    }

    return changed;
  }
}
