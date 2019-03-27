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
    currentState = State.RELEASE;
    if (!CLIMB.setOpenLoopFeedbackSensor(true)) {
      currentState = State.DONE;
    }
    CLIMB.setLowerLimit(ClimbSubsystem.kLowRelease);
    CLIMB.openLoop(ClimbSubsystem.kDownOpenLoopOutput);
  }

  @Override
  protected void execute() {
    switch (currentState) {
      case RELEASE:
        if (CLIMB.getStringpotPosition() >= ClimbSubsystem.kLowRelease - GOOD_ENOUGH) {
          CLIMB.setUpperLimit(ClimbSubsystem.kHighRelease);
          CLIMB.openLoop(ClimbSubsystem.kUpOpenLoopOutput);
          currentState = State.POSITION;
        }
        break;
      case POSITION:
        if (CLIMB.getStringpotPosition() <= ClimbSubsystem.kHighRelease + GOOD_ENOUGH) {
          currentState = State.DONE;
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
    CLIMB.stop();
  }

  private enum State {
    RELEASE,
    POSITION,
    DONE
  }
}
