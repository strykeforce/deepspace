package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class ClimbAutoCommand extends Command {
  VacuumSubsystem VACUUM = Robot.VACUUM;
  ClimbSubsystem CLIMB = Robot.CLIMB;

  ClimbState climbState;

  ClimbAutoCommand () {
    requires(VACUUM);
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    climbState = ClimbState.FAST_LOWER;
    CLIMB.openLoopMove(ClimbSubsystem.kLowerSpeed);
  }

  @Override
  protected void execute() {
    switch(climbState){
      case FAST_LOWER:
        if (CLIMB.getHeight() <= ClimbSubsystem.kHabHoverIn){
          climbState = ClimbState.FORM_SEAL;
          CLIMB.openLoopMove(ClimbSubsystem.kSealSpeed);
        }
        break;
      case FORM_SEAL:
        if (VACUUM.isClimbOnTarget()){
          climbState = ClimbState.FAST_CLIMB;
          CLIMB.enableRatchet();
          CLIMB.releaseKickstand();
          CLIMB.openLoopMove(ClimbSubsystem.kClimbSpeed);
        }

        if (CLIMB.getHeight() <= ClimbSubsystem.kTooLowIn){
          climbState = ClimbState.RESET;
          CLIMB.openLoopMove(ClimbSubsystem.kResetSpeed);
        }
        break;
      case FAST_CLIMB:
        if (CLIMB.getHeight() <= ClimbSubsystem.kClimbIn){
          climbState = ClimbState.DONE;
        }
        break;
      case RESET:
        if (CLIMB.getHeight() >= ClimbSubsystem.kHabHoverIn){
          climbState = ClimbState.FORM_SEAL;
          CLIMB.openLoopMove(ClimbSubsystem.kSealSpeed);
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

  private enum ClimbState{
    FAST_LOWER,
    FORM_SEAL,
    RESET,
    FAST_CLIMB,
    DONE;
  }
}
