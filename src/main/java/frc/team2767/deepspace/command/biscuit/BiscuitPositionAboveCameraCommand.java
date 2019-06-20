package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitPositionAboveCameraCommand extends Command {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public BiscuitPositionAboveCameraCommand() {
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    logger.info("STARTING BISCUIT ABOVE CAMERA");
    BISCUIT.setMotionMagicAccel(BiscuitSubsystem.kSlowAccel);
    if (VISION.action == Action.PLACE) {
      if (VISION.direction == FieldDirection.RIGHT) {
        BISCUIT.setPosition(BiscuitSubsystem.PLACE_RIGHT);
      } else {
        BISCUIT.setPosition(BiscuitSubsystem.PLACE_LEFT);
      }
    } else if (VISION.action == Action.PICKUP) {
      if (VISION.direction == FieldDirection.RIGHT) {
        BISCUIT.setPosition(BiscuitSubsystem.PICKUP_RIGHT);
      } else {
        BISCUIT.setPosition(BiscuitSubsystem.PICKUP_LEFT);
      }
    }
  }

  @Override
  protected boolean isFinished() {
    return BISCUIT.onTarget();
  }

  @Override
  protected void end() {
    logger.info("ENDING BISCUIT MOVEMENT");
  }
}
