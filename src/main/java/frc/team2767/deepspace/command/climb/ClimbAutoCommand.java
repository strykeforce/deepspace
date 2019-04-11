package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.Timer;
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
  private static final double PAUSE_WAIT = 0.2;
  private static final int GOOD_ENOUGH = 5;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double pauseInitTime;
  private ClimbState climbState;

  public ClimbAutoCommand() {
    requires(VACUUM);
    requires(CLIMB);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    logger.info("BEGIN CLIMB SEQUENCE");
    CLIMB.setSlowTalonConfig(true);
    climbState = ClimbState.FORM_SEAL;
    CLIMB.setVelocity(ClimbSubsystem.kSealOutputVelocity);
    logger.info("forming seal");
    //    CLIMB.setSlowTalonConfig(false);
    //    climbState = ClimbState.FAST_LOWER;
    //    CLIMB.setLowerLimit(ClimbSubsystem.kHabHover);
    //    CLIMB.setVelocity(ClimbSubsystem.kDownVelocity);
  }

  @SuppressWarnings("Duplicates")
  @Override
  protected void execute() {
    switch (climbState) {
      case FAST_LOWER:
        if (CLIMB.getStringPotPosition() >= ClimbSubsystem.kHabHover - GOOD_ENOUGH) {
          CLIMB.setSlowTalonConfig(true);
          climbState = ClimbState.FORM_SEAL;
          CLIMB.setVelocity(ClimbSubsystem.kSealOutputVelocity);
          logger.info("forming seal");
        }
        break;

      case FORM_SEAL:
        if (VACUUM.isClimbOnTarget()) {
          logger.info("fast climbing");
          CLIMB.setSlowTalonConfig(false);
          climbState = ClimbState.FAST_CLIMB;
          CLIMB.enableRatchet();
          CLIMB.releaseKickstand();
          VISION.startLightBlink(VisionSubsystem.LightPattern.CLIMB_GOOD);
          //          CLIMB.setLowerLimit(ClimbSubsystem.kClimb);
          CLIMB.setVelocity(ClimbSubsystem.kDownClimbVelocity);
        }

        if (CLIMB.getStringPotPosition() >= ClimbSubsystem.kTooLowIn - GOOD_ENOUGH) {
          logger.info("resetting");
          CLIMB.setSlowTalonConfig(false);
          climbState = ClimbState.RESET;
          //          CLIMB.setUpperLimit(ClimbSubsystem.kHabHover);
          CLIMB.setVelocity(ClimbSubsystem.kUpVelocity);
        }

        break;
      case FAST_CLIMB:
        if (CLIMB.getStringPotPosition() >= ClimbSubsystem.kClimb) {
          climbState = ClimbState.DONE;
          logger.info("done climbing");
        }
        break;
      case PAUSE:
        if (Timer.getFPGATimestamp() - pauseInitTime >= PAUSE_WAIT) {
          climbState = ClimbState.FORM_SEAL;
          CLIMB.setSlowTalonConfig(true);
          CLIMB.setVelocity(ClimbSubsystem.kSealOutputVelocity);
        }
        break;
      case RESET:
        if (VACUUM.isClimbOnTarget()) {
          logger.info("fast climbing during reset");
          CLIMB.setSlowTalonConfig(false);
          climbState = ClimbState.FAST_CLIMB;
          CLIMB.enableRatchet();
          CLIMB.releaseKickstand();
          VISION.startLightBlink(VisionSubsystem.LightPattern.CLIMB_GOOD);
          CLIMB.setVelocity(ClimbSubsystem.kDownClimbVelocity);
          break;
        }

        if (CLIMB.getStringPotPosition() <= ClimbSubsystem.kHabHover + GOOD_ENOUGH) {
          climbState = ClimbState.PAUSE;
          CLIMB.stop();
          pauseInitTime = Timer.getFPGATimestamp();
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
    PAUSE,
    FAST_CLIMB,
    DONE
  }
}
