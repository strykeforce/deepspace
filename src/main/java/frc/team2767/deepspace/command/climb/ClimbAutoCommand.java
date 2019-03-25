package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class ClimbAutoCommand extends Command {
  VacuumSubsystem VACUUM = Robot.VACUUM;
  ClimbSubsystem CLIMB = Robot.CLIMB;
  VisionSubsystem VISION = Robot.VISION;
  ClimbState climbState;

  public ClimbAutoCommand() {
    requires(VACUUM);
    requires(CLIMB);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    climbState = ClimbState.FAST_LOWER;
    CLIMB.setHeight(ClimbSubsystem.kHabHoverIn);
  }

  @Override
  protected void execute() {
    switch (climbState) {
      case FAST_LOWER:
        if (CLIMB.getHeight() <= ClimbSubsystem.kHabHoverIn) {
          climbState = ClimbState.FORM_SEAL;
          CLIMB.openLoopMove(ClimbSubsystem.kSealVelocity);
        }
        break;
      case FORM_SEAL:
        if (VACUUM.isClimbOnTarget()) {
          climbState = ClimbState.FAST_CLIMB;
          CLIMB.enableRatchet();
          CLIMB.releaseKickstand();
          VISION.startLightBlink(VisionSubsystem.LightPattern.CLIMB_GOOD);
          CLIMB.setHeight(ClimbSubsystem.kClimbIn);
        }
        if (CLIMB.getHeight() <= ClimbSubsystem.kTooLowIn) {
          climbState = ClimbState.RESET;
          CLIMB.setHeight(ClimbSubsystem.kHabHoverIn);
        }
        break;
      case FAST_CLIMB:
        if (CLIMB.getHeight() <= ClimbSubsystem.kClimbIn) {
          climbState = ClimbState.DONE;
        }
        break;
      case RESET:
        if (CLIMB.getHeight() >= ClimbSubsystem.kHabHoverIn) {
          climbState = ClimbState.FORM_SEAL;
          CLIMB.openLoopMove(ClimbSubsystem.kSealVelocity);
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
  }

  private enum ClimbState {
    FAST_LOWER,
    FORM_SEAL,
    RESET,
    FAST_CLIMB,
    DONE;
  }
}
