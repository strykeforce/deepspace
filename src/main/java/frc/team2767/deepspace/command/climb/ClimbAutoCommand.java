package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClimbAutoCommand extends Command {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final int GOOD_ENOUGH = 5;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private ClimbState climbState;

  public ClimbAutoCommand() {
    requires(VACUUM);
    requires(CLIMB);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    logger.info("BEGIN CLIMB SEQUENCE");
    if (!CLIMB.setOpenLoopFeedbackSensor(true)) {
      climbState = ClimbState.DONE;
    }
    climbState = ClimbState.FAST_LOWER;
    CLIMB.setLowerLimit(ClimbSubsystem.kHabHover);
    CLIMB.openLoop(ClimbSubsystem.kDownOpenLoopOutput);
  }

  @Override
  protected void execute() {
    switch (climbState) {
      case FAST_LOWER:
        if (CLIMB.getStringpotPosition() >= ClimbSubsystem.kHabHover - GOOD_ENOUGH) {
          if (!CLIMB.setOpenLoopFeedbackSensor(false)) {
            climbState = ClimbState.DONE;
          }
          climbState = ClimbState.FORM_SEAL;
          CLIMB.setVelocity(ClimbSubsystem.kSealOutputVelocity);
          logger.info("forming seal");
        }

        break;

      case FORM_SEAL:
        if (VACUUM.isClimbOnTarget()) {
          logger.info("fast climbing");
          if (!CLIMB.setOpenLoopFeedbackSensor(true)) {
            climbState = ClimbState.DONE;
          }
          climbState = ClimbState.FAST_CLIMB;
          CLIMB.enableRatchet();
          CLIMB.releaseKickstand();
          VISION.startLightBlink(VisionSubsystem.LightPattern.CLIMB_GOOD);
          CLIMB.setLowerLimit(ClimbSubsystem.kClimb);
          CLIMB.openLoop(ClimbSubsystem.kDownClimbOutput);
        }

        if (CLIMB.getStringpotPosition() >= ClimbSubsystem.kTooLowIn - GOOD_ENOUGH) {
          logger.info("resetting");
          if (!CLIMB.setOpenLoopFeedbackSensor(true)) {
            climbState = ClimbState.DONE;
          }
          climbState = ClimbState.RESET;
          CLIMB.setUpperLimit(ClimbSubsystem.kHabHover);
          CLIMB.openLoop(ClimbSubsystem.kUpOpenLoopOutput);
        }

        break;
      case FAST_CLIMB:
        if (CLIMB.getStringpotPosition() >= ClimbSubsystem.kClimb) {
          climbState = ClimbState.DONE;
          logger.info("done climbing");
        }
        break;
      case RESET:
        if (CLIMB.getStringpotPosition() <= ClimbSubsystem.kHabHover + GOOD_ENOUGH) {
          climbState = ClimbState.FORM_SEAL;
          if (!CLIMB.setOpenLoopFeedbackSensor(false)) {
            climbState = ClimbState.DONE;
          }
          CLIMB.setVelocity(ClimbSubsystem.kSealOutputVelocity);
        }
        break;
    }
  }

  @Override
  protected boolean isFinished() {
    return climbState == ClimbState.DONE;
  }

  @Override
  protected void end() {
    CLIMB.stop();
    logger.info("END CLIMB SEQUENCE");
  }

  private enum ClimbState {
    FAST_LOWER,
    FORM_SEAL,
    RESET,
    FAST_CLIMB,
    DONE
  }
}
