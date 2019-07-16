package frc.team2767.deepspace.command.sequences.pickup;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoconutPickupAutoRetryCommand extends Command {
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final double PRESSURE_DIFFERENTIAL = 2.5; // inHg
  private static final double STABLE_COUNTS = 5;
  private static final double WAIT_TIME = 1000; // ms
  private static final double DOWN_TIMEOUT = 1500;
  private static final double RESET_HEIGHT = 20.25;
  private static final double STRING_COMPRESSED_LENGTH = 223.0; // full length is 252
  private static double DOWN_SPEED = -0.35;
  private PickupState state;
  private double initialPressure;
  private double startSealTime;
  private boolean hasRetried;
  private double downInitTime;
  private double currentTime;
  private double currentPressure;
  private double stableCounts;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public CoconutPickupAutoRetryCommand() {
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    logger.info("Coconut Pickup Sequence");
    state = PickupState.DOWN;
    ELEVATOR.openLoopMove(DOWN_SPEED);
    initialPressure = VACUUM.getPressure();
    stableCounts = 0;
    hasRetried = false;
    downInitTime = System.currentTimeMillis();
  }

  @Override
  protected void execute() {
    switch (state) {
      case DOWN:
        if (BISCUIT.getCompression() <= STRING_COMPRESSED_LENGTH) {
          state = PickupState.WAIT_FOR_PRESSURE;
          ELEVATOR.setPosition(ELEVATOR.getPosition()); // inches
          startSealTime = System.currentTimeMillis();
          break;
        }

        // TIMEOUT (on down) = RESET & TRY AGAIN
        if (System.currentTimeMillis() - downInitTime > DOWN_TIMEOUT) {
          if (!hasRetried) {
            state = PickupState.RESET;
            hasRetried = true;
            ELEVATOR.setPosition(RESET_HEIGHT);
            logger.info("retrying");
            break;
          } else {
            state = PickupState.DONE;
            ELEVATOR.setPosition(RESET_HEIGHT);
            break;
          }
        }

        break;
      case WAIT_FOR_PRESSURE:
        currentTime = System.currentTimeMillis();
        currentPressure = VACUUM.getPressure();

        // TIMEOUT = RESET & TRY AGAIN
        if (currentTime - startSealTime > WAIT_TIME && !hasRetried) {
          state = PickupState.RESET;
          hasRetried = true;
          ELEVATOR.setPosition(RESET_HEIGHT);
          logger.info("retrying");
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
        currentPressure = VACUUM.getPressure();
        if (ELEVATOR.onTarget() && (currentPressure - initialPressure) < PRESSURE_DIFFERENTIAL) {
          startSealTime = System.currentTimeMillis();
          downInitTime = System.currentTimeMillis();
          ELEVATOR.openLoopMove(DOWN_SPEED);
          state = PickupState.DOWN;
          logger.info("Moving Down");
          break;
        }
        if (currentPressure - initialPressure > PRESSURE_DIFFERENTIAL) {
          state = PickupState.DONE;
          logger.info("Got Seal on RESET");
          break;
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
    WAIT_FOR_PRESSURE,
    RESET,
    DONE
  }
}
