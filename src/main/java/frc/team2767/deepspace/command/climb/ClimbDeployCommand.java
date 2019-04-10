package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClimbDeployCommand extends Command {

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private static final int GOOD_ENOUGH = 5;
  private State currentState;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ClimbDeployCommand() {
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    logger.debug("releasing");
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
          //          CLIMB.setUpperLimit(ClimbSubsystem.kHighRelease);
          CLIMB.openLoop(-1.0); // kUpVel
          currentState = State.POSITION;
          logger.debug("released, climber position = {}", CLIMB.getStringPotPosition());
        }
        break;
      case POSITION:
        if (CLIMB.getStringPotPosition() <= ClimbSubsystem.kHighRelease + GOOD_ENOUGH) {
          logger.debug("climber position = {}", CLIMB.getStringPotPosition());
          currentState = State.DONE;
          CLIMB.stop();
        }

        break;
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
    DONE
  }
}
