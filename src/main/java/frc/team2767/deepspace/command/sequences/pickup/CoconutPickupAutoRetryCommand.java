package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoconutPickupAutoRetryCommand extends Command {
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static PickupState state;
  private static double initialPressure;
  private static double startSealTime;
  private static boolean hasRetried;
  private static double currentTime;
  private static double currentPressure;
  private static double stableCounts;

  private static double DOWN_SPEED = -0.23;
  private static double PRESSURE_DIFFERENTIAL = 2.5; // inHg
  private static double STABLE_COUNTS = 5;
  private static double WAIT_TIME = 1000; // ms
  private static double RESET_HEIGHT = 20.25;

  public CoconutPickupAutoRetryCommand() {
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    logger.info("Coconut Pickup Sequence");
    state = PickupState.DOWN;
    ELEVATOR.openLoopMove(DOWN_SPEED);
    startSealTime = System.currentTimeMillis();
    initialPressure = VACUUM.getPressure();
    stableCounts = 0;
    hasRetried = false;
  }

  @Override
  protected void execute() {
    switch (state) {
      case DOWN:
        currentTime = System.currentTimeMillis();
        currentPressure = VACUUM.getPressure();
        // TIMEOUT = RESET & TRY AGAIN
        if (currentTime - startSealTime > WAIT_TIME && !hasRetried) {
          state = PickupState.RESET;
          hasRetried = true;
          ELEVATOR.setPosition(RESET_HEIGHT);
          logger.info("Retrying");
          break;
        }

        // UPDATE STABLE COUNTS
        if ((currentPressure - initialPressure) > PRESSURE_DIFFERENTIAL) stableCounts++;
        else stableCounts = 0.0;

        // TIMEOUT OR STABLE = DONE
        if (stableCounts >= STABLE_COUNTS
            || (hasRetried && currentTime - startSealTime > WAIT_TIME)) {
          logger.info("Pickup Sequence Done");
          state = PickupState.DONE;
        }
        break;
      case RESET:
        if (ELEVATOR.onTarget() && (currentPressure - initialPressure) < PRESSURE_DIFFERENTIAL) {
          startSealTime = System.currentTimeMillis();
          ELEVATOR.openLoopMove(DOWN_SPEED);
          state = PickupState.DOWN;
          logger.info("Moving Down");
        }
    }
  }

  @Override
  protected boolean isFinished() {
    return state == PickupState.DONE;
  }

  @Override
  protected void end() {
    ELEVATOR.stop();
  }

  private enum PickupState {
    DOWN,
    RESET,
    DONE
  }
}
