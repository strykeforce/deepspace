package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClimbLevel2DeployCommand extends Command {

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private static final int GOOD_ENOUGH = 5;
  private State currentState;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ClimbLevel2DeployCommand() {
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    logger.info("Releasing - Lvl 2 Climb");
    currentState = State.RELEASE;
    CLIMB.setSlowTalonConfig(false);
    CLIMB.setSlowTalonConfig(false);
    CLIMB.setVelocity(ClimbSubsystem.kDownVelocity);
  }

  @Override
  protected void execute() {
    switch (currentState) {
      case RELEASE:
        if (CLIMB.getStringPotPosition() >= ClimbSubsystem.kLowRelease - GOOD_ENOUGH) {
          currentState = State.POSITION;
          CLIMB.setVelocity(ClimbSubsystem.kUpVelocity);
          logger.info("Released: Position = {}", CLIMB.getStringPotPosition());
        }
        break;
      case POSITION:
        if (CLIMB.getStringPotPosition() <= ClimbSubsystem.kHighLvl2 + GOOD_ENOUGH) {
          logger.info("climber position = {}", CLIMB.getStringPotPosition());
          currentState = State.SLOW_DOWN;
          CLIMB.setSlowTalonConfig(true);
          CLIMB.setVelocity(ClimbSubsystem.kSealOutputVelocity);
        }
        break;
      case SLOW_DOWN:
        if (CLIMB.getStringPotPosition() >= ClimbSubsystem.kHabHoverLvl2 - GOOD_ENOUGH) {
          CLIMB.stop();
          currentState = State.DONE;
          logger.info("Climb Deployed: Position = {}", CLIMB.getStringPotPosition());
        }
        break;
    }
  }

  @Override
  protected boolean isFinished() {
    return currentState == State.DONE;
  }

  private enum State {
    RELEASE,
    POSITION,
    SLOW_DOWN,
    DONE
  }
}

// 58
