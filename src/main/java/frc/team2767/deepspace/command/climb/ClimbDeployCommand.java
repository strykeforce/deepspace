package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClimbDeployCommand extends Command {

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final int GOOD_ENOUGH = 5;
  private State currentState;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ClimbDeployCommand() {
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    logger.info("releasing");
    currentState = State.RELEASE;
    CLIMB.setSlowTalonConfig(false);
    CLIMB.setSlowTalonConfig(false);
    //    CLIMB.setLowerLimit(ClimbSubsystem.kLowRelease);
    CLIMB.setVelocity(ClimbSubsystem.kDownVelocity);
  }

  @Override
  protected void execute() {
    switch (currentState) {
      case RELEASE:
        if (CLIMB.getStringPotPosition() >= ClimbSubsystem.kLowRelease - GOOD_ENOUGH) {
          CLIMB.openLoop(-1.0); // kUpVel
          currentState = State.POSITION;
          logger.info("released, climber position = {}", CLIMB.getStringPotPosition());
        }
        break;
      case POSITION:
        if (CLIMB.getStringPotPosition() <= ClimbSubsystem.kHighRelease + GOOD_ENOUGH) {
          logger.info("climber position = {}", CLIMB.getStringPotPosition());
          currentState = State.SLOW_DOWN;
          CLIMB.setSlowTalonConfig(true);
          CLIMB.setVelocity(ClimbSubsystem.kSealOutputVelocity);
        }

        break;
      case SLOW_DOWN:
        if (CLIMB.getStringPotPosition() >= ClimbSubsystem.kHabHover - GOOD_ENOUGH) {
          CLIMB.stop();
          currentState = State.DONE;
          logger.info("Climb Deployed, position = {}", CLIMB.getStringPotPosition());
        }
    }
  }

  @Override
  protected boolean isFinished() {
    return currentState == State.DONE;
  }

  @Override
  protected void end() {
    logger.debug("climb deploy finished");
    //    CLIMB.stop();
  }

  private enum State {
    RELEASE,
    POSITION,
    SLOW_DOWN,
    DONE
  }
}
