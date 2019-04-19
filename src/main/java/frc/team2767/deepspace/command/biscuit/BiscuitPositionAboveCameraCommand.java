package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class BiscuitPositionAboveCameraCommand extends Command {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final VisionSubsystem VISION = Robot.VISION;

  private static final double PLACE_RIGHT = 55.0;
  private static final double PLACE_LEFT = -55.0;
  private static final double PICKUP_RIGHT = 65.0;
  private static final double PICKUP_LEFT = -65.0;

  public BiscuitPositionAboveCameraCommand() {
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    BISCUIT.setMotionMagicAccel(BiscuitSubsystem.kSlowAccel);
    if (VISION.action == Action.PLACE) {
      if (VISION.direction == FieldDirection.RIGHT) {
        BISCUIT.setPosition(PLACE_RIGHT);
      } else {
        BISCUIT.setPosition(PLACE_LEFT);
      }
    } else if (VISION.action == Action.PICKUP) {
      if (VISION.direction == FieldDirection.RIGHT) {
        BISCUIT.setPosition(PICKUP_RIGHT);
      } else {
        BISCUIT.setPosition(PICKUP_LEFT);
      }
    }
  }

  @Override
  protected boolean isFinished() {
    return BISCUIT.onTarget();
  }
}
